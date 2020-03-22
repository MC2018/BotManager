var onloadCallback = function() {
    var objects = $(".col-auto.mt-4.listcell.center");
    var result = "";

    objects.each(function(index) {
        var name = $(this).find(".game-name").text().replace(/\,/g, "");
        var id = $(this).find("a").attr("data-url");
        var count = $(this).find("p").text().trim().split(" ")[0].replace(/,/g, "");

        //result += name + "," + id + "," + count + "\n";

        if (name.includes("+")) {
            result += name.replace(/\+/g, "and") + "," + id + "," + count + "\n";
            result += name.replace(/\+/g, "plus") + "," + id + "," + count + "\n";
        } else if (name.includes("&")) {
            result += name.replace(/&/g, "and") + "," + id + "," + count + "\n";
        } else {
            result += name + "," + id + "," + count + "\n";
        }

        if (id.includes("_")) {
            result += id.replace(/_/g, "") + "," + id + "," + count + "\n";
        }

        result += id + "," + id + "," + count + "\n";
    });

    console.log(result);
};