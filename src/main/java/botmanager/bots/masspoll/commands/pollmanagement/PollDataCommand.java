package botmanager.bots.masspoll.commands.pollmanagement;

import botmanager.bots.masspoll.MassPoll;
import botmanager.bots.masspoll.generic.MassPollCommandBase;
import botmanager.bots.masspoll.objects.Poll;
import botmanager.generic.commands.IPrivateMessageReceivedCommand;
import botmanager.utils.IOUtils;
import botmanager.utils.Utils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.*;
import java.util.ArrayList;

public class PollDataCommand extends MassPollCommandBase implements IPrivateMessageReceivedCommand {

    public final String[] KEYWORDS = {
            "polldata"
    };

    public final String[] EXCEL_KEYWORDS = {
            "excel"
    };

    public PollDataCommand(MassPoll bot) {
        super(bot);
    }

    @Override
    public void runOnPrivateMessage(PrivateMessageReceivedEvent event) {
        String message = Utils.startsWithReplace(event.getMessage().getContentRaw(), KEYWORDS);
        ArrayList<Poll.PollUserData> pollUserData;
        ArrayList<String> options;
        MessageChannel channel;
        Poll poll;
        long pollID;
        boolean makeSpreadsheet = false;

        if (message == null) {
            return;
        }

        try {
            pollID = Long.parseLong(message);
        } catch (NumberFormatException e) {
            try {
                pollID = Long.parseLong(Utils.startsWithReplace(message, EXCEL_KEYWORDS));
                makeSpreadsheet = true;
            } catch (NumberFormatException er) {
                return;
            }
        }

        while (bot.pollsInProcess.contains(pollID)) {
        }

        bot.pollsInProcess.add(pollID);

        try {
            poll = IOUtils.readGson(Poll.getFileLocation(bot, pollID), Poll.class);
        } catch (IOException e) {
            bot.pollsInProcess.remove(pollID);
            return;
        }

        bot.pollsInProcess.remove(pollID);

        if (makeSpreadsheet) {
            HSSFWorkbook workbook = new HSSFWorkbook();
            HSSFSheet sheet = workbook.createSheet("Data");
            ByteArrayOutputStream baos = null;
            InputStream is = null;
            HSSFRow headRow;
            int rowIndex = 0, columnIndex = 0;

            pollUserData = poll.getUserDataCopy();
            options = poll.getOptions();

            headRow = sheet.createRow(rowIndex++);
            headRow.createCell(columnIndex++).setCellValue("Username");
            headRow.createCell(columnIndex++).setCellValue("Nickname");

            for (int i = 0; i < poll.getOptionsSize(); i++) {
                headRow.createCell(columnIndex++).setCellValue(options.get(i));
            }

            headRow.createCell(columnIndex++).setCellValue("Comments");

            for (int i = 0; i < pollUserData.size(); i++) {
                Poll.PollUserData userData = pollUserData.get(i);
                HSSFRow row = sheet.createRow(rowIndex++);
                columnIndex = 0;

                row.createCell(columnIndex++).setCellValue(userData.username);
                row.createCell(columnIndex++).setCellValue(userData.nickname);

                for (int j = 0; j < poll.getOptionsSize(); j++) {
                    row.createCell(columnIndex++).setCellValue((userData.votes & (1 << j)) > 0 ? "Yes" : "");
                }

                for (int j = 0; j < userData.comments.size(); j++) {
                    row.createCell(columnIndex++).setCellValue(userData.comments.get(j));
                }
            }

            try {
                channel = event.getAuthor().openPrivateChannel().complete();

                baos = new ByteArrayOutputStream();
                workbook.write(baos);
                is = new ByteArrayInputStream(baos.toByteArray());
                channel.sendFile(is, "Poll " + pollID + ".xls").queue();
            } catch (IOException e) {
                System.out.println("Realistically this error should never occur");
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    workbook.close();

                    if (is != null) {
                        is.close();
                    }

                    if (baos != null) {
                        baos.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            EmbedBuilder builder = new EmbedBuilder();
            int[] results;
            int participantCount = 0, totalComments = 0;

            pollUserData = poll.getUserDataCopy();
            options = poll.getOptions();
            results = new int[options.size()];

            for (int i = 0; i < pollUserData.size(); i++) {
                if (pollUserData.get(i).votes > 0) {
                    participantCount++;

                    for (int j = 0; j < options.size(); j++) {
                        if ((pollUserData.get(i).votes & (1 << j)) > 0) {
                            results[j]++;
                        }
                    }
                }

                totalComments += pollUserData.get(i).comments.size();
            }

            builder.setTitle("Poll " + pollID + " Simple Stats");
            builder.setDescription(poll.getQuestion());
            builder.addField("Total Responses", participantCount + " out of " + pollUserData.size(), false);

            for (int i = 0; i < options.size(); i++) {
                builder.addField(options.get(i), "" + results[i], false);
            }

            if (totalComments > 0) {
                builder.addField("", totalComments + " Comment" + (totalComments > 1 ? "s" : ""), false);
            }

            channel = event.getAuthor().openPrivateChannel().complete();
            channel.sendMessageEmbeds(builder.build()).queue();
        }
    }

}
