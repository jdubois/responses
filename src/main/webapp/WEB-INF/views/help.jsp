<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=utf-8" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title>Responses - Aide</title>
    <jsp:include page="../fragments/html_head.jsp"/>
</head>
<body id="main">
<div id="banner"></div><div id="container">
    <jsp:include page="../fragments/header.jsp"/>
    <div id="content">
        <h1>Aide de Responses</h1>

        <h2>Qu'est ce que Responses?</h2>

        <p>
            Responses est un logiciel en ligne permettant de facilement retrouver des questions et des r&eacute;ponses,
            gr&acirc;ce &agrave; un syst&egrave;me intelligent d'&eacute;tiquettes et de votes, coupl&eacute; &agrave;
            un moteur de recherche sp&eacute;cialis&eacute;.
        </p>

        <h2>Quelles questions peut-on poser sur Responses?</h2>

        <p>A l'heure actuelle, Responses est sp&eacute;cialis&eacute; dans le d&eacute;veloppement informatique: vous pouvez
        y poser des questions concernant votre langage de programmation pr&eacute;f&eacute;r&eacute;, votre environnement de d&eacute;veloppement,
        les frameworks que vous utilisez... Par exemple, des questions sur l'utilisation de Java, Spring ou Tomcat sont toutes &agrave; fait
        pertinentes.</p>

        <h2>Comment fonctionnent les &eacute;tiquettes?</h2>

        <p>
            A chaque question peuvent &ecirc;tre affect&eacute;es de 0 &agrave; 5 &eacute;tiquettes. Ces &eacute;tiquettes
            permettent ensuite de classifier les questions, par exemple :
            <ul>
                <li>Une question poss&eacute;de les &eacute;tiquettes "aa", "bb", "cc", "dd"</li>
                <li>Cette question sera alors visible par les utilisateurs suivant (entre autres) les &eacute;tiquettes "aa", "bb", "cc" ou "dd"</li>
                <li>Elle sera &eacute;galement visible des utilisateurs ne voulant voir qu'un ensemble restreint d'&eacute;tiquettes, tel que
                "aa", "bb" et "cc"</li>
                <li>Le moteur de recherche privil&eacute;giera cette question si les mot-clefs "aa", "bb", "cc" ou "dd" lui sont demand&eacute;s</li>
            </ul>
        </p>

        <h2>Comment fonctionnent les votes?</h2>

        <p>
            Chaque utilisateur peut voter pour ou contre des questions et des r&eacute;ponses.
        </p>
        <p>
            Pour les questions, ces votes servent d'indication aux utilisateurs sur la pertinence de la question. Ils permettent &eacute;galement
            de rendre une question plus difficile ou plus facile à trouver. Par exemple, le moteur de recherche ne retourne pas (par d&eacute;faut)
            les questions qui ont un vote n&eacute;gatif : si une majorit&eacute; d'utilisateurs pensent qu'une question est mauvaise, elle va
            donc dispara&icirc;tre des r&eacute;sultats.
        </p>
        <p>
            Pour les r&eacute;ponses, leur ordre d'affichage est d&eacute;termin&eacute; par les votes : les meilleures r&eacute;ponses
            sont propos&eacute;es en premier aux utilisateurs. Comme pour les questions, les r&eacute;ponses de mauvaises qualit&eacute;
            sont donc plus difficiles &agrave; trouver.
        </p>
    </div>
    <jsp:include page="../fragments/footer.jsp"/>
</div>
</body>
</html>
