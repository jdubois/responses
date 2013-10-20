//Page initialization
var tabs;
$(document).ready(function() {
    $('ul.sf-menu').superfish();
    $("#add_sel_tag").autocomplete(baseUrl + "/tag/search", {
        width: 130,
        cacheLength: 50
    });
    $("#add_fav_tag").autocomplete(baseUrl + "/tag/search", {
        width: 130,
        cacheLength: 50
    });
    $("#add_ign_tag").autocomplete(baseUrl + "/tag/search", {
        width: 130,
        cacheLength: 50
    });
    decorateQuestions();
    $("#menu_search_field").keyup(function(event) {
        if (event.keyCode == 13) {
            searchQuestions();
        }
    });
    $("#menu_search_field").autocomplete(baseUrl + "/suggest", {
        selectFirst: false,
        width: 300,
        cacheLength: 50
    });
});

//Messages and errors
var isMessageShowed = false;
function showMessage(text) {
    isMessageShowed = true;
    $("#message-text").html("<strong>Information:</strong> " + text);
    $("#message").effect("bounce", {}, 250);
    setTimeout("hideMessage()", 5000);
}
function hideMessage() {
    isMessageShowed = false;
    $("#message").effect("blind", {}, 500);
    $("#message-text").html("");
}
var isErrorShowed = false;
function showError(text) {
    isErrorShowed = true;
    $("#error-text").html("<strong>Erreur:</strong> " + text);
    $("#error").effect("bounce", {}, 250);
}
$("#error").ajaxError(function(event, request, settings){
   showError("Une erreur technique s'est produite, votre action a &eacute;t&eacute; annul&eacute;e.")
 });
function hideError() {
    isErrorShowed = false;
    $("#error").effect("blind", {}, 500);
    $("#error-text").html("");
}
var isWelcomeShowed = false;
function showWelcome() {
    isWelcomeShowed = true;
    $("#welcome-text").html("Est-ce votre premi&egrave;re visite sur com.github.jdubois.responses.net? "+
            "com.github.jdubois.responses.net est un site Internet gratuit d'entraide et de partage de la connaissance technique. "+
            "N'h&eacute;sitez pas &agrave; <a href=\"" + context + "/authentication/registration\">cr&eacute;er un compte</a> pour pouvoir participer! "+
            "Vous pouvez aussi consulter <a href=\"" + baseUrl + "/help\">l'aide en ligne</a>.");
    $("#welcome").effect("bounce", {}, 250);
}
function hideWelcome() {
    isWelcomeShowed = false;
    $("#welcome").effect("blind", {}, 500);
    $("#welcome-text").html("");
}

function reload() {
    window.location.href=window.location.href;
}

//Authentication
function authentication() {
    window.location.href = context + "/?targetUrl=" + encodeURIComponent(window.location.href);
}

function searchQuestions() {
    var query = $("#menu_search_field").val();
    var location = baseUrl + "/search?q=" + query;
    var sortBy = $("#search_sort_by").val();
    if (sortBy != null && sortBy != 0) {
        location = location + "&sort=" + sortBy;
    }
    if ($("#search_showNegativeQuestions:checked").length > 0) {
        location = location + "&showNegativeQuestions=true";
    }
    var index;
    window.location.href = location;
}

//Questions display management
function previousQuestions() {
    if (questionIndex > 20) {
        if (selectedTagsList.length == 0) {
            window.location.href = baseUrl + "?index=" + (questionIndex - 20);
        } else {
            window.location.href = baseUrl + "/tagged/" + selectedTagsList + "?index=" + (questionIndex - 20);
        }
    } else if (questionIndex == 20) {
        if (selectedTagsList.length == 0) {
            window.location.href = baseUrl;
        } else {
            window.location.href = baseUrl + "/tagged/" + selectedTagsList;
        }
    }
}
function decorateQuestions() {
    var favTagsArray = new Array();
    $("#favoriteTags").find(".tag").each(function(i, selected) {
        favTagsArray.push($(selected).text());
    });
    $(".question").each(function(i, selected) {
        $(selected).find(".questionTitle").removeClass("questionTitle-favorite");
        $(selected).find(".questionTag").each(function(i2, selected2) {
            var tag = $(selected2).text();
            for (key in favTagsArray) {
                if (favTagsArray[key] == tag) {
                    $(selected).find(".questionTitle").addClass("questionTitle-favorite");
                }
            }
        });
    });
    var ignTagsArray = new Array();
    $("#ignoredTags").find(".tag").each(function(i, selected) {
        ignTagsArray.push($(selected).text());
    });
    $(".question").each(function(i, selected) {
        $(selected).show();
        $(selected).find(".questionTag").each(function(i2, selected2) {
            var tag = $(selected2).text();
            for (key in ignTagsArray) {
                if (ignTagsArray[key] == tag) {
                    $(selected).hide();
                }
            }
        });
    });
}

//Top questions management
function showTopQuestions(type, when) {
    if (selectedTagsList === "") {
        tabs.tabs('url', 2, baseUrl + "/top-questions?type=" + type + "&when=" + when);
    } else {
        tabs.tabs('url', 2, baseUrl + "/top-questions/" + selectedTagsList + "?type=" + type + "&when=" + when);
    }
    tabs.tabs('load', 2);
}

//Tag management
function validateTag(text) {
    if (text == "") {
        showError("L'&eacute;tiquette choisie est vide.");
        return false;
    }
    var pattern = new RegExp("[^a-z0-9-]{1,20}");
    if (pattern.test(text)) {
        showError("<br/>Une &eacute;tiquette ne doit pas:<ul><li>comporter plus de 20 caract&egrave;res, et </li><li>ne peut &ecirc;tre compos&eacute;e que de caract&egrave;res alphab&eacute;tiques minuscules, de chiffres ou du caract&egrave;re \"-\".</li></ul>Par exemple, \"test\", \"test-2\" et \"test-test\" sont des &eacute;tiquettes correctes.");
        return false;
    } else {
        if (isErrorShowed) {
            hideError();
        }
        return true;
    }
}
function displayTags(data, node, removeFunction) {
    if (data.length > 0) {
        var tags = data.split("\+");
        var displayTags = "";
        for (key in tags) {
            displayTags += "<a href=\"" + baseUrl + "/tagged/" + tags[key] + "\"><span class=\"tag\">" + tags[key] + "</span></a>" +
                           "<span class=\"tag_delete\" " +
                           "onmouseover=\"$(this).addClass('tag_delete_h')\" " +
                           "onmouseout=\"$(this).removeClass('tag_delete_h')\" " +
                           "onclick=\"" + removeFunction + "('" + tags[key] + "')\">x</span><br/>";
        }
        node.html(displayTags);
    } else {
        node.html("");
    }
    decorateQuestions();
}
//Sort the tags array alphabetically, used by the selected tags functions.
function sortTagsList() {
    var tagsArray = selectedTagsList.split("\+");
    tagsArray.sort();
    selectedTagsList = "";
    for (key in tagsArray) {
        selectedTagsList += tagsArray[key] + "+";
    }
    selectedTagsList = selectedTagsList.substring(0, selectedTagsList.length - 1);
}
function addSelectedTag(tag) {
    if (selectedTagsList.split("\+").length > 4) {
        showMessage("Le nombre maximum d'&eacute;tiquettes s&eacute;lectionnables est de 5.");
    } else {
        if (selectedTagsList.length == 0) {
            selectedTagsList = tag;
        } else {
            selectedTagsList += "+" + tag;
        }
        sortTagsList();
        window.location.href = baseUrl + "/tagged/" + selectedTagsList;
    }
}
function removeSelectedTag(text) {
    var tagsArray = selectedTagsList.split("\+");
    selectedTagsList = "";
    for (key in tagsArray) {
        if (tagsArray[key] != text) {
            selectedTagsList += tagsArray[key] + "+";
        }
    }
    selectedTagsList = selectedTagsList.substring(0, selectedTagsList.length - 1);
    sortTagsList();
    if (selectedTagsList.length == 0) {
        window.location.href = baseUrl;
    } else {
        window.location.href = baseUrl + "/tagged/" + selectedTagsList;
    }
}
function addFavoriteTag() {
    var tag = $('#add_fav_tag').val();
    if (validateTag(tag)) {
        $.get(baseUrl + "/tag/create/favorite/" + tag, {},
                function(data) {
                    displayTags(data, $("#favoriteTags"), "removeFavoriteTag");
                }, "text");
        $('#add_fav_tag').val("");
    }
}
function removeFavoriteTag(text) {
    $.get(baseUrl + "/tag/delete/favorite/" + text, {},
            function(data) {
                displayTags(data, $("#favoriteTags"), "removeFavoriteTag");
            }, "text");
    $('#add_fav_tag').val("");
}
function addIgnoredTag() {
    var tag = $('#add_ign_tag').val();
    if (validateTag(tag)) {
        $.get(baseUrl + "/tag/create/ignored/" + tag, {},
                function(data) {
                    displayTags(data, $("#ignoredTags"), "removeIgnoredTag");
                }, "text");
        $('#add_ign_tag').val("");
    }
}
function removeIgnoredTag(text) {
    $.get(baseUrl + "/tag/delete/ignored/" + text, {},
            function(data) {
                displayTags(data, $("#ignoredTags"), "removeIgnoredTag");
            }, "text");
    $('#add_ign_tag').val("");
}

//Edit the tags of an existing question
function addQuestionTag(questionId) {
    var tag = $('#add_question_tag').val();
    $.get(baseUrl + "/question/" + questionId + "/tag/add/" + tag, {},
            function(data) {
                if (data.length == 0) {
                    reload();
                } else {
                    showError(data);
                }
            }, "text");
}

function removeQuestionTag(questionId, tag) {
    $.get(baseUrl + "/question/" + questionId + "/tag/delete/" + tag, {},
            function(data) {
                if (data.length == 0) {
                    reload();
                } else {
                    showError(data);
                }
            }, "text");

}

//Question votes
function vote_u_h(id) {
    $("#vote_u_" + id).removeClass("vote_u");
    $("#vote_u_" + id).addClass("vote_u_h");
}
function vote_u(id) {
    $("#vote_u_" + id).removeClass("vote_u_h");
    $("#vote_u_" + id).addClass("vote_u");
}
function vote_d_h(id) {
    $("#vote_d_" + id).removeClass("vote_d");
    $("#vote_d_" + id).addClass("vote_d_h");
}
function vote_d(id) {
    $("#vote_d_" + id).removeClass("vote_d_h");
    $("#vote_d_" + id).addClass("vote_d");
}
function voteForQuestion(questionId, vote) {
    $.get(baseUrl + "/vote/question/" + questionId + "/" + vote, {},
            function(data) {
                if (data == "error") {
                    showError("Une erreur s'est produite, le vote a &eacute;t&eacute; annul&eacute;.");
                } else if (data == "author") {
                    showMessage("Vous ne pouvez pas voter pour une question dont vous &ecirc;tes l'auteur.");
                } else {
                    $("#vote_t_" + questionId).text(data);
                    if (vote != 0) {
                        $("#vote_t_" + questionId).removeClass("vote_t");
                        $("#vote_t_" + questionId).addClass("vote_t_h");
                    } else {
                        $("#vote_t_" + questionId).removeClass("vote_t_h");
                        $("#vote_t_" + questionId).addClass("vote_t");
                    }
                    if (vote == 1) {
                        $("#vote_u_" + questionId).replaceWith("<td id='vote_u_" + questionId + "' class='vote_u_h' " +
                                                               "onMouseOut='vote_u_h(" + questionId + ")' " +
                                                               "onClick='voteForQuestion(" + questionId + ", 0)'></td>");
                    } else {
                        $("#vote_u_" + questionId).replaceWith("<td id='vote_u_" + questionId + "' class='vote_u' " +
                                                               "onMouseOver='vote_u_h(" + questionId + ")' " +
                                                               "onMouseOut='vote_u(" + questionId + ")' " +
                                                               "onClick='voteForQuestion(" + questionId + ", 1)'></td>");
                    }
                    if (vote == -1) {
                        $("#vote_d_" + questionId).replaceWith("<td id='vote_d_" + questionId + "' class='vote_d_h' " +
                                                               "onMouseOut='vote_d_h(" + questionId + ")' " +
                                                               "onClick='voteForQuestion(" + questionId + ", 0)'></td>");
                    } else {
                        $("#vote_d_" + questionId).replaceWith("<td id='vote_d_" + questionId + "' class='vote_d' " +
                                                               "onMouseOver='vote_d_h(" + questionId + ")' " +
                                                               "onMouseOut='vote_d(" + questionId + ")' " +
                                                               "onClick='voteForQuestion(" + questionId + ", -1)'></td>");
                    }
               }
               voteQuestionToolTips();
            }, "text");
}
function voteForAnswer(answerId, vote) {
    $.get(baseUrl + "/vote/answer/" + answerId + "/" + vote, {},
            function(data) {
                if (data == "error") {
                    showError("Une erreur s'est produite, le vote a &eacute;t&eacute; annul&eacute;.");
                } else if (data == "author") {
                    showMessage("Vous ne pouvez pas voter pour une r&eacute;ponse dont vous &ecirc;tes l'auteur.");
                } else {
                    $("#vote_t_a_" + answerId).text(data);
                    if (vote != 0) {
                        $("#vote_t_a_" + answerId).removeClass("vote_t");
                        $("#vote_t_a_" + answerId).addClass("vote_t_h");
                    } else {
                        $("#vote_t_a_" + answerId).removeClass("vote_t_h");
                        $("#vote_t_a_" + answerId).addClass("vote_t");
                    }
                    if (vote == 1) {
                        $("#vote_u_a_" + answerId).replaceWith("<td id='vote_u_a_" + answerId + "' class='vote_u_h' " +
                                                               "onMouseOut=\"vote_u_h('a_" + answerId + "')\" " +
                                                               "onClick='voteForAnswer(" + answerId + ", 0)'></td>");
                    } else {
                        $("#vote_u_a_" + answerId).replaceWith("<td id='vote_u_a_" + answerId + "' class='vote_u' " +
                                                               "onMouseOver=\"vote_u_h('a_" + answerId + "')\" " +
                                                               "onMouseOut=\"vote_u('a_" + answerId + "')\" " +
                                                               "onClick='voteForAnswer(" + answerId + ", 1)'></td>");
                    }
                    if (vote == -1) {
                        $("#vote_d_a_" + answerId).replaceWith("<td id='vote_d_a_" + answerId + "' class='vote_d_h' " +
                                                               "onMouseOut=\"vote_d_h('a_" + answerId + "')\" " +
                                                               "onClick='voteForAnswer(" + answerId + ", 0)'></td>");
                    } else {
                        $("#vote_d_a_" + answerId).replaceWith("<td id='vote_d_a_" + answerId + "' class='vote_d' " +
                                                               "onMouseOver=\"vote_d_h('a_" + answerId + "')\" " +
                                                               "onMouseOut=\"vote_d('a_" + answerId + "')\" " +
                                                               "onClick='voteForAnswer(" + answerId + ", -1)'></td>");
                    }
                }
            }, "text");
}

// Ask question functions
function cancelQuestion() {
    if (selectedTagsList.length == 0) {
        window.location.href = baseUrl;
    } else {
        window.location.href = baseUrl + "/tagged/" + selectedTagsList;
    }
}
function askQuestionStep1() {
    $('#ask_question_1').show();
    $('#ask_question_2').hide();
}
function validateQuestion() {
    if ($('#questionTitle').val().length == 0) {
        showError("Il n'y a aucune question pos&eacute;e.");
        return false;
    }
    if ($('#questionTitle').val().length > 140) {
        showError("La question ne doit pas faire plus de 140 caract&egrave;res.");
        return false;
    }
    if ($('#questionText').html().length > 10000) {
        showError("La description de la question fait plus de 10 000 caract&egrave;res.");
        return false;
    }
    if (isErrorShowed) {
        hideError();
    }
    return true;
}
function previewQuestion() {
    $('#previewTitle').val($('#questionTitle').val());
    $('#previewText').val($('#questionText').val());
    window.open("","preview","width=800,height=500,menubar=no,status=no,scrollbars=yes");
    document.forms["previewQuestionForm"].submit();
}
function previewAnswer() {
    $('#previewText').val($('#answerText').val());
    window.open("","preview","width=800,height=500,menubar=no,status=no,scrollbars=yes");
    document.forms["previewAnswerForm"].submit();
}
function askQuestionStep2() {
    if (validateQuestion()) {
        $('#ask_question_1').hide();
        $('#ask_question_2').show();
    }
}

var ask_tag_list = "";
var ask_tag_count = 0;
function addAskTag() {
    var tag = $('#add_ask_tag').val();
    if (validateTag(tag)) {
         internalAddAskTag(tag);
    }
}
function internalAddAskTag(tag) {
    if ($("#ask_tags_tip").is(':visible')) {
        $("#ask_tags_tip").effect("blind", {}, 500);
    }
    ask_tag_list += tag + "+";
    $("#askTags").val(ask_tag_list);
    displayTags(ask_tag_list.substring(0, ask_tag_list.length - 1), $("#ask_tags"), "remove_ask_tag");
    $('#add_ask_tag').val("");
    ask_tag_count++;
    if (ask_tag_count >= 5) {
        $("#add_ask_tag_div").hide();
        showMessage("<p>Vous ne pouvez utiliser que 5 &eacute;tiquettes au maximum.</p>")
    }
}
function remove_ask_tag(ask_tag) {
    ask_tag_list = ask_tag_list.replace(ask_tag + "+", "");
    $("#askTags").val(ask_tag_list);
    displayTags(ask_tag_list.substring(0, ask_tag_list.length - 1), $("#ask_tags"), "remove_ask_tag");
    ask_tag_count--;
    if (ask_tag_count < 5) {
        $("#add_ask_tag_div").show();
    }
}
function askQuestionSubmit() {
    $('#ask_question_2').hide();
    document.forms["askQuestionForm"].submit();
}
function cancelEditQuestion() {
    window.location.href = $('#questionUrl').val();
}
function doEditQuestion() {
    if (validateQuestion()) {
        document.forms["editQuestionForm"].submit();
    }
}

//Question views count
function countQuestionView(questionId) {
    $.get(baseUrl + "/question/count?questionId=" + questionId, {},
            function(data) {
            }, "text");
}

//Answer question functions
function validateAnswerQuestion() {
    if ($('#answerText').html().length == 0) {
        showError("Votre r&eacute;ponse est vide.");
        return false;
    }
    if ($('#answerText').html().length > 10000) {
        showError("La r&eacute;ponse fait plus de 10 000 caract&egrave;res.");
        return false;
    }
    if (isErrorShowed) {
        hideError();
    }
    return true;
}
function answerQuestion(questionId) {
    if (validateAnswerQuestion()) {
        $('#answer_question').hide();
        document.forms["answerQuestionForm"].submit();
    }
}
function doEditAnswer() {
    if (validateAnswerQuestion()) {
        document.forms["answerQuestionForm"].submit();
    }
}


//Answer actions
function best_answer(answerId) {
    $.get(baseUrl + "/answer/best?answerId=" + answerId, {},
            function(data) {
                $('.display-html-best-answer-title').hide();
                if (data == "ok") {
                    $('.display-html').removeClass('display-html-best-answer');
                    $('#text_' + answerId).addClass('display-html-best-answer');
                } else if (data == "unselect") {
                    $('.display-html').removeClass('display-html-best-answer');
                } else {
                    showError("Une erreur s'est produite.");
                }
            }, "text")
}

//Abuse management
function abuseQuestion(id) {
    window.location.href = context + '/about/contact?abuse=question&id=' + id + "&abuseUrl=" + encodeURIComponent(window.location.href);
}
function abuseAnswer(id) {
    window.location.href = context + '/about/contact?abuse=answer&id=' + id + "&abuseUrl=" + encodeURIComponent(window.location.href);
}

//Comments management
var comment_selected = -2;
function comment_select(id) {
    $(".qa-actions-comment").slideUp(400);
    if (comment_selected == id) {
        comment_selected = -2;
    } else {
        $('#comment_' + id).slideDown(400);
        comment_selected = id;
    }
}

function send_comment_answer(answerId) {
    var value = $('#comment_value_' + answerId).val();
    if (value.length > 500) {
        showError("Un commentaire ne peut pas faire plus de 500 caract&egrave;res.");
    } else {
        $.get(baseUrl + "/answer/comment?answerId=" + answerId + "&value=" + value, {},
                function(data) {
                    $('#comment_value_' + answerId).val("");
                    comment_select(answerId);
                    show_comments_answer_callback(data, answerId);
                }, "json");
    }
}
function show_comments_answer(answerId) {
    $.get(baseUrl + "/answer/showComments?answerId=" + answerId, {}, function(data) {
        show_comments_answer_callback(data, answerId)
    }, "json");
}
function show_comments_answer_callback(data, answerId) {
    $('#answer-comments-container_' + answerId).html('');
    $('#answer-comments-container_' + answerId).hide();
    $('#answer-comments-img_' + answerId).show();
    $.each(data.commentDtoList, function(i, dto) {
        $('#answer-comments-container_' + answerId)
                .append('<div class="answer-comments">' +
                        '<a href="#a_' + dto.id + '">' + (i + 1) +'</a> ' +
                        dto.value + ' - ' +
                        '<a href="' + baseUrl + '/profile/' + dto.userId + '/' + dto.userUrl + '">' +
                        dto.userFirstName + ' ' + dto.userLastName + '</a></div>');
    });
    $('#answer-comments-container_' + answerId).slideDown(400);
}

function send_comment_question(questionId) {
    var value = $('#comment_value_-1').val();
    if (value.length > 500) {
        showError("Un commentaire ne peut pas faire plus de 500 caract&egrave;res.");
    } else {
        $.get(baseUrl + "/question/comment?questionId=" + questionId + "&value=" + value, {},
                function(data) {
                    comment_select(questionId);
                    $('#comment_value_-1').val("");
                    show_comments_question_callback(data);},
                "json");
    }
}
function show_comments_question(questionId) {
    $.get(baseUrl + "/question/showComments?questionId=" + questionId, {}, function(data) {show_comments_question_callback(data)}, "json");
}
function show_comments_question_callback(data) {
    $('#question-comments-container').html('');
    $('#question-comments-container').hide();
    $('#question-comments-img').show();
    $.each(data.commentDtoList, function(i, dto) {
        $('#question-comments-container')
                .append('<div class="question-comments">' +
                        '<a href="#c_' + dto.id + '">' + (i + 1) +'</a> ' +
                        dto.value + ' - ' +
                        '<a href="' + baseUrl + '/profile/' + dto.userId + '/' + dto.userUrl + '"><b>' +
                        dto.userFirstName + ' ' + dto.userLastName + '</b></a></div>');
    });
    $('#question-comments-container').slideDown(400);
}

//Watchs management
function watch_question(questionId) {
    $.get(baseUrl + "/watch?questionId=" + questionId + "&value=1", {},
            function(data) {
               if (data == "watch") {
                   $("#alert_u").show();
                   $("#alert").hide();
               }
            }, "text")
}

function unwatch_question(questionId) {
    $.get(baseUrl + "/watch?questionId=" + questionId + "&value=0", {},
            function(data) {
               if (data == "unwatch") {
                   $("#alert_u").hide();
                   $("#alert").show();
               }
            }, "text")
}
isSupportShowed = false;
function showSupport(questionId) {
    if (isSupportShowed) {
        $("#support-wrapper").html("");
        $("#support-wrapper").slideUp(400);
        isSupportShowed = false;
    } else {
        $.get(baseUrl + "/question/support/" + questionId, {},
                function(data) {
                    $("#support-wrapper").html(data);
                    $("#support-wrapper").slideDown(400);
                    isSupportShowed = true;
                }, "html");
    }
}
function support(questionId, step, user) {
    $.post(baseUrl + "/question/support/" + questionId, { step: step, userId: user } ,
                function(data) {
                    $("#support-wrapper").html(data);
                    $("#supportMessage").html("<b>Support</b>");
                }, "html");
}

//Admin management
function editUser(userId) {
     $.get(baseUrl + "/admin/user/" + userId, { } ,
                function(data) {
                    $("#userId").val(data.userDto.id);
                    $("#email").val(data.userDto.email);
                    $("#firstName").val(data.userDto.firstName);
                    $("#lastName").val(data.userDto.lastName);
                    if (data.userDto.enabled == false) {
                        $('input[name="enabled"]')[0].checked = false;
                        $('input[name="enabled"]')[1].checked = true;
                    } else {
                        $('input[name="enabled"]')[0].checked = true;
                        $('input[name="enabled"]')[1].checked = false;
                    }
                    $('input[name="isModerator"]').attr('checked', false);
                    $('input[name="isSupport"]').attr('checked', false);
                    $('input[name="isAdmin"]').attr('checked', false);
                    for (roleId in data.userDto.roles) {
                        role = data.userDto.roles[roleId];
                        if (role == "ROLE_MODERATOR") {
                            $('input[name="isModerator"]').attr('checked', true);
                        } else if (role == "ROLE_SUPPORT") {
                            $('input[name="isSupport"]').attr('checked', true);
                        } else if (role == "ROLE_ADMIN") {
                            $('input[name="isAdmin"]').attr('checked', true);
                        }
                    }
                    $(".selectableUser").animate({ backgroundColor: "#d7d7ff" }, 100);
                    $("#p_" + userId).animate({ backgroundColor: "#6560ab" }, 100);
                }, "json");
}

//Tooltips
function questionTagTooltips() {
    $('.questionTag').tooltip({
        track: true,
        delay: 0,
        bodyHandler: function() {
            return 'Montrer toutes les questions avec l\'&eacute;tiquette \'' + $(this).text() + '\'';
        },
        fade: 250
    });
}
function tagToolTips() {
    $('.tag').tooltip({
        track: true,
        delay: 0,
        bodyHandler: function() {
            return 'Montrer toutes les questions avec l\'&eacute;tiquette \'' + $(this).text() + '\'';
        },
        fade: 250
    });
    $('.tag_delete').tooltip({
        track: true,
        delay: 0,
        bodyHandler: function() {
            return 'Supprimer cette &eacute;tiquette';
        },
        fade: 250
    });
    $('.tag_add_img').tooltip({
        track: true,
        delay: 0,
        showURL: false, 
        bodyHandler: function() {
            return 'Ajouter cette &eacute;tiquette';
        },
        fade: 250
    });
}

function voteQuestionToolTips() {
    $('.vote_u_u').tooltip({
        track: true,
        delay: 0,
        bodyHandler: function() {
            return 'Vous ne pouvez pas voter pour cette question';
        },
        fade: 250
    });
    $('.vote_d_u').tooltip({
        track: true,
        delay: 0,
        bodyHandler: function() {
            return 'Vous ne pouvez pas voter contre cette question';
        },
        fade: 250
    });
    $('.vote_u').tooltip({
        track: true,
        delay: 0,
        bodyHandler: function() {
            return 'Voter pour cette question';
        },
        fade: 250
    });
    $('.vote_d').tooltip({
        track: true,
        delay: 0,
        bodyHandler: function() {
            return 'Voter contre cette question';
        },
        fade: 250
    });
    $('.vote_u_h').tooltip({
        track: true,
        delay: 0,
        bodyHandler: function() {
            return 'Ne plus voter pour cette question';
        },
        fade: 250
    });
    $('.vote_d_h').tooltip({
        track: true,
        delay: 0,
        bodyHandler: function() {
            return 'Ne plus voter contre cette question';
        },
        fade: 250
    });
}
function voteAnswerToolTips() {
    $('.question-answer-info .vote_u_u').tooltip({
        track: true,
        delay: 0,
        bodyHandler: function() {
            return 'Vous ne pouvez pas voter pour cette réponse';
        },
        fade: 250
    });
    $('.question-answer-info .vote_d_u').tooltip({
        track: true,
        delay: 0,
        bodyHandler: function() {
            return 'Vous ne pouvez pas voter contre cette réponse';
        },
        fade: 250
    });
    $('.question-answer-info .vote_u').tooltip({
        track: true,
        delay: 0,
        bodyHandler: function() {
            return 'Voter pour cette réponse';
        },
        fade: 250
    });
    $('.question-answer-info .vote_d').tooltip({
        track: true,
        delay: 0,
        bodyHandler: function() {
            return 'Voter contre cette réponse';
        },
        fade: 250
    });
    $('.question-answer-info .vote_u_h').tooltip({
        track: true,
        delay: 0,
        bodyHandler: function() {
            return 'Ne plus voter pour cette réponse';
        },
        fade: 250
    });
    $('.question-answer-info .vote_d_h').tooltip({
        track: true,
        delay: 0,
        bodyHandler: function() {
            return 'Ne plus voter contre cette réponse';
        },
        fade: 250
    });
}