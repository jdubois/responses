function Save_Button_onclick() {
    var lang = document.getElementById("ProgrammingLangauges").value;
    var code = WrapCode(lang);
    code = code + document.getElementById("CodeArea").value;
    code = code + "</pre>";

    if (document.getElementById("CodeArea").value == '') {
        tinyMCEPopup.close();
        return false;
    }
    tinyMCEPopup.execCommand('mceInsertContent', false, code);
    tinyMCEPopup.close();
}

function WrapCode(lang)
{

    return "<pre class=\"brush: " + lang + "\">";
}

function Cancel_Button_onclick()
{
    tinyMCEPopup.close();
    return false;
}
