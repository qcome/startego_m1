<%--
  Created by IntelliJ IDEA.
  User: root
  Date: 11/14/16
  Time: 1:03 AM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sb" uri="/struts-bootstrap-tags" %>
<%@ taglib prefix="sj" uri="/struts-jquery-tags" %>
<%@ page session="false" %>


<html>
<head>
    <title>Page de connexion</title>
    <sj:head/>
    <sb:head/>
    <link href="${pageContext.request.contextPath}/resources/style.css" rel="stylesheet" type="text/css" >
    <link href="${pageContext.request.contextPath}/resources/fonts/LobsterTwo-Regular.ttf" rel="stylesheet" type="text/css" >
    <link href="${pageContext.request.contextPath}/resources/fonts/LobsterTwo-Italic.ttf" rel="stylesheet" type="text/css" >
    <link href="${pageContext.request.contextPath}/resources/fonts/BADABB__.woff" rel="stylesheet" type="text/css" >
    <link href="https://fonts.googleapis.com/css?family=Lobster+Two" rel="stylesheet" type="text/css" >
    <script type="text/javascript" src="${pageContext.request.contextPath}/resources/javascript.js"></script>

</head>
<body>
    <script>
        $(document).ready(function()
        {

            $('.registration').submit(function (e)
            {
                e.preventDefault();
                var data = $(this).serialize();

                $.ajax({
                    url: "registration.action?" + data,
                    type: "POST",
                    contentType: "application/json: charset=utf-8",
                    dataType: "json",
                    success: function (result)
                    {
                        var error = result.error;
                        if(error == "")
                        {
                            $('#errorRegistration').html('').removeClass('alert alert-danger actionError');
                            $('#successRegistration').html('<br/>Inscription validée.');

                            var timer = 5;
                            setInterval(function refreshSuccessRegistration()
                            {
                                timer--;
                                if (timer == 0)
                                {
                                    $('#successRegistration').html('');
                                    clearInterval(refreshSuccessRegistration);
                                }
                            }, 1000);
                        }
                        else
                        {
                            $('#errorRegistration').html(error).attr('class', 'alert alert-danger actionError')
                                    .css('color', 'black')
                                    .css('font-size', '18px')
                                    .css('margin-bottom', '10px');

                            if($('#errorConnexion').html() != undefined)
                                $('#errorConnexion').html('').removeClass('alert alert-danger actionError');

                        }
                    },
                    error: function ()
                    {
                    }
                });
            });
        });
    </script>

    <div class="navbar navbar-default navbar-fixed-top" role="navigation" id="navigationbar">
        <div class="container-fluid">
            <div class="navbar-header">
                <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target=".navbar-collapse">
                    <span class="sr-only">Toggle Navigation</span>
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                </button>
                <a class="navbar-brand" href="#index" style="font-family:'BadaBoom BB';font-size:32px">STRATEGO 2016</a>
            </div>
            <div class="navbar-collapse collapse">
                <nav>
                    <ul class="nav navbar-nav navbar-right" >
                        <li><a href="#index">Accueil &nbsp; <span class="glyphicon glyphicon-home" aria-hidden="true"></span></a></li>
                        <li><a href="#connexion">Connexion &nbsp;<span class="glyphicon glyphicon-user" aria-hidden="true"></span></a></li>
                        <li><a href="#inscription">S'inscrire &nbsp;<span class="glyphicon glyphicon-hand-right" aria-hidden="true"></span></a></li>
                        <li><a href="#regles">Règles &nbsp; <span class="glyphicon glyphicon-book" aria-hidden="true"></span></a></li>
                    </ul>
                </nav>
            </div>
        </div>
    </div>

    <div id="index">
        <div class="row" id="indexIntro" >
            <div class="container-fluid">
                <img src="${pageContext.request.contextPath}/resources/images/stratego-header-home-transparent.png" id="imgIntro">
                <h3 style="font-family:'BadaBoom BB';font-size: 42px;text-shadow: -1px 0 white, 0 1px white, 1px 0 white, 0 -1px white;";>Jouez au celebre jeu de strategie avec vos amis. Une inscription est necessaire pour utiliser l'application web et le client.</h3>
            </div>
        </div>
    </div>

    <div id="connexion">
        <div class="container-fluid" id="formConnex">
            <img src="${pageContext.request.contextPath}/resources/images/User.png" id="user">
            <div class="row">
                <div class="col-md-offset-4 col-md-4">
                    <h3 style="font-family:'BadaBoom BB';color:#09b6bc;font-weight:normal;font-size:42px;text-shadow: -1px 0 white, 0 1px white, 1px 0 white, 0 -1px white;">Connexion</h3>

                    <s:form action="menu" theme="bootstrap" cssClass="formConnex">
                        <s:textfield
                                id="inputIndex"
                                name="login"
                                placeholder="Login"/>
                        <s:password
                                id="inputIndex1"
                                name="password"
                                placeholder="Mot de passe"/>
                        <s:fielderror fieldName="connexion" id="errorConnexion"/>
                        <s:submit id="btn-submit" cssClass="btn btn-primary pull-right connexion-button" value="Connexion"/>
                    </s:form>

                </div>
            </div>
        </div>
    </div>

    <div id="inscription">
        <div class="container-fluid" id="formInscription">
            <div class="row">
                <div class="col-md-offset-4 col-md-4">
                    <h3 style="font-family:'BadaBoom BB';color:#09b6bc;font-weight:normal;font-size:42px;text-shadow: -1px 0 white, 0 1px white, 1px 0 white, 0 -1px white;">Pas encore inscrit ?</h3>

                    <s:form action="registration" theme="bootstrap" cssClass="registration formConnex">
                        <s:textfield
                                id="inputIndex2"
                                name="login"
                                placeholder="Login"/>
                        <s:password
                                id="inputIndex3"
                                name="password"
                                placeholder="Mot de passe"/>
                        <s:password
                                id="inputIndex4"
                                name="passwordConfirmation"
                                placeholder="Confirmation mot de passe"/>
                        <div id="errorRegistration">
                        </div>
                        <s:submit id="btn-submit" cssClass="btn btn-primary pull-right connexion-button" value="Valider"/>
                        <div id="successRegistration" style="color:white;font-size:18px;text-align:center;margin-left:-40px;">
                        </div>
                    </s:form>
                </div>
            </div>
        </div>
    </div>

    <div id="regles">
        <div class="container-fluid">
            <div class ="row">
                <div class="col-md-offset-2 col-md-8 col-md-offset-2" id="rules" >


                    <div class="well" id="well"> <h3 style="font-family:'BadaBoom BB';color:#09b6bc;font-weight:normal;font-size:42px;text-shadow: -1px 0 white, 0 1px white, 1px 0 white, 0 -1px white;">Les regles <br/><BR>  </h3>Le Stratego se joue à 2 joueurs (un joueur avec les pièces rouges, l'autre avec les pièces bleues) sur un plateau carré de 92 cases (10 cases de côté moins 2 lacs carrés de 4 cases chacun). Chaque joueur possède 40 pièces. <br/><BR>
                        Les pièces représentent des unités militaires et ont deux faces. Une face ne peut être vue que par un seul joueur à la fois, l'autre ne voyant que la couleur de la pièce.<br/><BR> Les pièces sont placées de telle façon que le joueur ne voit que le rang de ses propres pièces.
                        Au début de la partie chaque joueur dispose ses pièces comme il l'entend sur ses quatre premières rangées.<br/><BR> Cette pré-phase du jeu est stratégique et déterminante pour la suite de la partie.
                        Chaque joueur déplace une pièce d'une case par tour: à gauche, à droite, en avant ou en arrière (pas en diagonale). Une attaque se produit quand le joueur déplace sa pièce sur une case déjà occupée par l'adversaire. Chaque joueur montre alors sa pièce à l'adversaire. <br/><BR>La pièce la plus forte reste en jeu, l'autre est éliminée ; en cas d'égalité, les deux sont éliminées.

                        <br/><BR>Le but du jeu est de capturer le Drapeau de l'adversaire ou d'éliminer assez de pièces adverses afin que l'adversaire ne puisse plus faire de déplacements.
                    </div>

                </div>
            </div>
        </div>
    </div>
</body>
</html>
