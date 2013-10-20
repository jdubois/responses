$('textarea.tinymce').tinymce({
 script_url : '${context}/static/jquery/tinymce-3.3.2/tiny_mce.js',
 // General options
 entity_encoding : "raw",
 language : "fr",
 button_tile_map : true,
 theme : "advanced",
 extended_valid_elements: "textarea[name|class|cols|rows]",
 remove_linebreaks : false,
 plugins : "safari,style,table,advhr,iespell,inlinepopups,insertdatetime,preview,searchreplace,print,contextmenu,paste,fullscreen,visualchars,nonbreaking,xhtmlxtras,template,codehighlighting",

 // Theme options
 theme_advanced_buttons1 : "codehighlighting,bold,italic,underline,strikethrough,|,justifyleft,justifycenter,justifyright,justifyfull,formatselect,fontselect,fontsizeselect,|,hr,charmap,|,iespell,removeformat,|,print,preview,fullscreen",
 theme_advanced_buttons2 : "cut,copy,paste,pastetext,pasteword,|,search,replace,|,bullist,numlist,|,outdent,indent,|,undo,redo,|,link,unlink,|,insertdate,inserttime,|,forecolor,backcolor",
 theme_advanced_buttons3 : "tablecontrols",
 theme_advanced_toolbar_location : "top",
 theme_advanced_toolbar_align : "left",
 theme_advanced_statusbar_location : "bottom"
});