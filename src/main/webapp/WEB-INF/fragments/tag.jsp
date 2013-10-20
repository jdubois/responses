<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

        <p class="box_title">Etiquettes</p>
        <c:if test="${popularTags != null}">
            <p>
           <span><img src="${staticContent}/style/tag_blue.png" height="16" width="16" alt="tags"/>
           <b>&nbsp;S&eacute;lectionn&eacute;es&nbsp;&nbsp;</b></span>
            </p>

            <c:if test="${selectedTags != null}">
                <p id="selectedTags">
                    <c:forEach var="tag" items="${selectedTags}">
                        <a href="${baseUrl}/tagged/${tag}"><span class="tag">${tag}</span></a>
                                <span class='tag_delete' onmouseover="$(this).addClass('tag_delete_h')"
                                      onmouseout="$(this).removeClass('tag_delete_h')"
                                      onclick="removeSelectedTag('${tag}')">x</span><br/>
                    </c:forEach>
                </p>
            </c:if>
            <div>
                <input id="add_sel_tag" maxlength="20" class="tag_auto_complete"/>
                <img src="${staticContent}/style/add.png" class="tag_add_img"
                     onclick="addSelectedTag($('#add_sel_tag').val())" alt="add"/>
            </div>
            <br/>

            <c:if test="${fn:length(popularTags) > 0}">
                <p>
                <span><img src="${staticContent}/style/tag_green.png" height="16" width="16" alt="popular"/>
                        <b>&nbsp;Populaires&nbsp;&nbsp;</b></span>
                </p>

                <p id="popularTags">
                    <c:forEach var="tag" items="${popularTags}">
                        <a href="${baseUrl}/tagged/${tag.text}"><span
                                class="tag">${tag.text}</span></a> (${tag.size})
                        <img src="${staticContent}/style/add.png" class="tag_add_img"
                             onclick="addSelectedTag('${tag.text}')" alt="add"/><br/>
                    </c:forEach>
                </p>
                <br/>
            </c:if>
        </c:if>
        <sec:authorize ifNotGranted="ROLE_USER">
            <p>Si vous &eacute;tiez <a href="javascript:authentication();">authentifi&eacute;</a>, vous pourriez poser des questions, y r&eacute;pondre, les commenter, s&eacute;lectionner
                des &eacute;tiquettes favorites ou ignor&eacute;es...</p>
        </sec:authorize>
        <sec:authorize ifAnyGranted="ROLE_USER">
            <p>
            <span><img src="${staticContent}/style/tag_blue_add.png" class="tag_add_img" alt=""/>
                    <b>&nbsp;Favorites&nbsp;&nbsp;&nbsp;</b></span>
            </p>

            <p id="favoriteTags">
                <c:forEach var="tag" items="${favoriteTags}">
                    <a href="${baseUrl}/tagged/${tag}"><span class="tag">${tag}</span></a>
                    <span class='tag_delete' onmouseover="$(this).addClass('tag_delete_h')"
                          onmouseout="$(this).removeClass('tag_delete_h')"
                          onclick="removeFavoriteTag('${tag}')"/>x</span><br/>
                </c:forEach>
            </p>

            <div>
                <input id="add_fav_tag" maxlength="20" class="tag_auto_complete"/>
                <img src="${staticContent}/style/add.png" class="tag_add_img" onclick="addFavoriteTag()" alt=""/>
            </div>

            <p>
            <span><img src="${staticContent}/style/tag_blue_delete.png" height="16" width="16" alt=""/>
                    <b>&nbsp;Ignor&eacute;es&nbsp;&nbsp;&nbsp;</b></span>
            </p>

            <p id="ignoredTags">
                <c:forEach var="tag" items="${ignoredTags}">
                    <a href="${baseUrl}/tagged/${tag}"><span class="tag">${tag}</span></a>
                    <span class='tag_delete' onmouseover="$(this).addClass('tag_delete_h')"
                          onmouseout="$(this).removeClass('tag_delete_h')"
                          onclick="removeIgnoredTag('${tag}')">x</span><br/>
                </c:forEach>
            </p>

            <div>
                <input id="add_ign_tag" maxlength="20" class="tag_auto_complete"/>
                <img src="${staticContent}/style/add.png"  class="tag_add_img" onclick="addIgnoredTag()"
                     alt=""/>
            </div>
            <br/>
        </sec:authorize>
