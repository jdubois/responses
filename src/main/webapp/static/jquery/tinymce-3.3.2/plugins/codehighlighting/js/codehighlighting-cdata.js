function Save_Button_onclick() {
    var lang = document.getElementById("ProgrammingLangauges").value;
    var code = WrapCode(lang);
    code = code + document.getElementById("CodeArea").value;
    code = code + "]]></script>"
    if (document.getElementById("CodeArea").value == '') {
        tinyMCEPopup.close();
        return false;
    }
    tinyMCEPopup.execCommand('mceInsertContent', false, code);
    tinyMCEPopup.close();
}

function WrapCode(lang)
{

    return "<script type=\"syntaxhighlighter\" class=\"brush: " + lang + "\"><![CDATA[";
}

function Cancel_Button_onclick()
{
    tinyMCEPopup.close();
    return false;
}
