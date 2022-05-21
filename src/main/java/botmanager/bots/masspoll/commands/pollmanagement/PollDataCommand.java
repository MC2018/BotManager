package botmanager.bots.masspoll.commands.pollmanagement;

import botmanager.bots.masspoll.MassPoll;
import botmanager.bots.masspoll.generic.MassPollCommandBase;
import botmanager.bots.masspoll.objects.Poll;
import botmanager.bots.masspoll.objects.PollAccessor;
import botmanager.generic.commands.IPrivateMessageReceivedCommand;
import botmanager.utils.Utils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;

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

        try (PollAccessor pollAccesser = new PollAccessor(bot, pollID, PollAccessor.PollAccessType.POLL_CREATOR_ID, event.getAuthor().getId())) {
            poll = pollAccesser.getPoll();
        } catch (Exception e) {
            return;
        }

        if (!poll.getCreatorID().equals(event.getAuthor().getId())) {
            return;
        }

        if (makeSpreadsheet) {
            HSSFWorkbook workbook = new HSSFWorkbook();
            HSSFSheet sheet = workbook.createSheet("Data");
            HSSFCellStyle yesStyle = workbook.createCellStyle(), noStyle = workbook.createCellStyle(), boldStyle = workbook.createCellStyle();
            HSSFFont boldFont = workbook.createFont();
            ByteArrayOutputStream baos = null;
            InputStream is = null;
            HSSFRow headerRow, footerRow;
            HSSFCell cell;
            int[] resultsCounter = new int[poll.getOptionsSize()];
            int rowIndex = 0, columnIndex = 0;

            pollUserData = poll.getUserDataCopy();
            options = poll.getOptions();

            yesStyle.setFillForegroundColor(IndexedColors.LIME.getIndex());
            yesStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            noStyle.setFillForegroundColor(IndexedColors.CORAL.getIndex());
            noStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            boldFont.setBold(true);
            boldStyle.setFont(boldFont);

            headerRow = sheet.createRow(rowIndex++);
            headerRow.createCell(columnIndex++).setCellValue("Username");
            headerRow.createCell(columnIndex++).setCellValue("Nickname");

            for (int i = 0; i < poll.getOptionsSize(); i++) {
                headerRow.createCell(columnIndex++).setCellValue(options.get(i));
            }

            headerRow.createCell(columnIndex++).setCellValue("Comments");

            for (int i = 0; i < pollUserData.size(); i++) {
                Poll.PollUserData userData = pollUserData.get(i);
                HSSFRow row = sheet.createRow(rowIndex++);
                columnIndex = 0;

                row.createCell(columnIndex++).setCellValue(userData.username);
                row.createCell(columnIndex++).setCellValue(userData.nickname);

                for (int j = 0; j < poll.getOptionsSize(); j++) {
                    boolean votedYes = (userData.votes & (1 << j)) > 0;

                    cell = row.createCell(columnIndex++);
                    cell.setCellValue(votedYes ? "Yes" : "No");
                    cell.setCellStyle(votedYes ? yesStyle : noStyle);

                    if (votedYes) {
                        resultsCounter[j]++;
                    }
                }

                for (int j = 0; j < userData.comments.size(); j++) {
                    row.createCell(columnIndex++).setCellValue(userData.comments.get(j));
                }
            }

            footerRow = sheet.createRow(rowIndex++);
            cell = footerRow.createCell(0);
            cell.setCellValue("Total:");
            cell.setCellStyle(boldStyle);
            cell = footerRow.createCell(1);
            cell.setCellValue(poll.getUserDataCopy().size());
            cell.setCellStyle(boldStyle);
            columnIndex = 2;

            for (int i = 0; i < poll.getOptionsSize(); i++) {
                cell = footerRow.createCell(columnIndex++);
                cell.setCellStyle(boldStyle);
                cell.setCellValue(resultsCounter[i]);
            }

            try {
                channel = event.getChannel();

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
            String comment = "";
            int[] results;
            int participantCount = 0, commentOnlyParticipantCount = 0, totalComments = 0;

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
                } else if (!pollUserData.get(i).comments.isEmpty()) {
                    commentOnlyParticipantCount++;
                }

                totalComments += pollUserData.get(i).comments.size();
            }

            if (commentOnlyParticipantCount > 0) {
                comment = "\n(" + commentOnlyParticipantCount + " of which commented w/o voting)";
            }

            builder.setTitle("Poll " + pollID + " Simple Stats");
            builder.setDescription(poll.getQuestion());
            builder.addField("Total Responses",
                    (participantCount + commentOnlyParticipantCount) + " out of " + pollUserData.size() + " user" + (pollUserData.size() == 1 ? "" : "s") + comment,
                    false);

            for (int i = 0; i < options.size(); i++) {
                builder.addField(options.get(i), "" + results[i], false);
            }

            if (totalComments > 0) {
                builder.addField("", totalComments + " Comment" + (totalComments == 1 ? "" : "s"), false);
            }

            channel = event.getAuthor().openPrivateChannel().complete();
            channel.sendMessageEmbeds(builder.build()).queue();
        }
    }

}
