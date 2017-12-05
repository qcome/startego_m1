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

<html>
<head>
    <title>Menu</title>
    <sj:head/>
    <sb:head/>

    <link href="${pageContext.request.contextPath}/resources/loading.css" rel="stylesheet" >
    <link href="${pageContext.request.contextPath}/resources/style.css" rel="stylesheet" type="text/css" >
    <link href="${pageContext.request.contextPath}/resources/fonts/LobsterTwo-Regular.ttf" rel="stylesheet" type="text/css" >
    <link href="${pageContext.request.contextPath}/resources/fonts/LobsterTwo-Italic.ttf" rel="stylesheet" type="text/css" >
    <link href="${pageContext.request.contextPath}/resources/fonts/BADABB__.woff" rel="stylesheet" type="text/css" >
    <link href="https://fonts.googleapis.com/css?family=Lobster+Two" rel="stylesheet" type="text/css" >
    <script type="text/javascript" src="${pageContext.request.contextPath}/resources/javascript.js"></script>

</head>
<body>
    <script>
        var login;
        var cptTimeout = 0;
        $(document).ready(function()
        {
            $('.declineInvitation').submit(function(e)
            {
                e.preventDefault();
                var data = $(this).serialize();

                $.ajax({
                    url: "declineInvitation.action?" + data,
                    type: "POST",
                    contentType: "application/json: charset=utf-8",
                    dataType: "json",
                    success: function(result)
                    {
                        $('tr#' + result.position.toString() + '-invitation').remove();
                    },
                    error: function()
                    {
                        alert('Erreur!');
                    }
                });
            });
            login = $('#player')[0].innerHTML.split('>')[2].split('<')[0];

            setInterval('refresh()', 5000);
        });

        function refresh()
        {
            $.ajax({
                url: "refreshMenu.action?login=" + login,
                type: "POST",
                contentType: "application/json: charset=utf-8",
                dataType: "json",
                alsuccess: function(result)
                {
                    cptTimeout++;
                    if(cptTimeout === 24)
                    {
                        //alert("Vous avez été déconnecté pour inactivité...");
                        window.location = "disconnection.action?login=" + login;
                    }

                    var numberOfInvitations = document.getElementById('invitations').rows.length;
                    var i = 0;
                    for (var invitation in result.invitations)
                    {
                        var formInvitation =
                            '<td>' + invitation + '</td>' +
                            '<td>' +
                                '<form id="acceptInvitation" class="acceptInvitation" name="acceptInvitation" ' +
                                'action="/acceptInvitation.action" method="post">' +
                                    '<fieldset>' +
                                        '<input id="acceptInvitation_login" name="login" value="' + login + '" type="hidden"> </input>' +
                                        '<input id="acceptInvitation_loginAdversary" name="loginAdversary" value="' + invitation + '" type="hidden"> </input>' +
                                        '<input id="acceptInvitation_0" class="btn btn-primary center-block " value="Accepter" type="submit"> </input>' +
                                    '</fieldset>' +
                                '</form>' +
                            '</td>' +
                            '<td>' +
                                '<form id="declineInvitation" class="declineInvitation" name="declineInvitation" ' +
                                'action="/declineInvitation.action" method="post">' +
                                    '<fieldset>' +
                                        '<input id="declineInvitation_login" name="login" value="' + login + '" type="hidden"> </input>' +
                                        '<input id="declineInvitation_loginAdversary" name="loginAdversary" value="' + invitation + '" type="hidden"> </input>' +
                                        '<input id="declineInvitation_position" name="position" value="' + i.toString() + '" type="hidden"> </input>' +
                                        '<input id="declineInvitation_0" class="btn btn-primary decline-button" value="Décliner" type="submit"> </input>' +
                                    '</fieldset>' +
                                '</form>' +
                            '</td>';
                        if (i < numberOfInvitations)
                            $('#' + i + '-invitation').html(formInvitation);
                        else
                        {
                            $('#invitations').append('<tr id=' + numberOfInvitations.toString() + '-invitation>' + formInvitation + '</tr>');
                            numberOfInvitations++;
                        }
                        i++;
                    }

                    $('.declineInvitation').submit(function (e)
                    {
                        e.preventDefault();
                        var data = $(this).serialize();

                        $.ajax({
                            url: "declineInvitation.action?" + data,
                            type: "POST",
                            contentType: "application/json: charset=utf-8",
                            dataType: "json",
                            success: function (result)
                            {
                                $('tr#' + result.position.toString() + '-invitation').remove();
                            },
                            error: function ()
                            {
                                alert('Erreur!');
                            }
                        });
                    });

                    var numberOfPlayersOnline = $('#selectPlayer option').length;
                    var k = 1;
                    for (var player in result.playersOnline)
                    {
                        if (player !== login)
                        {
                            if (k < numberOfPlayersOnline +1 )
                            {
                                $('#' + k + '-playerSelect').html(player);
                            }
                            else
                            {
                                $('#selectPlayer').append('<option id=' + k + '-playerSelect' +
                                    ' value="'+player+'">'+player+'</option>');
                                numberOfPlayersOnline++;
                            }
                            k++;
                        }
                    }
                    while($('#' + k + '-playerSelect').length)
                    {
                        $('#' + k + '-playerSelect').remove();
                        k++;
                    }

                    var numberOfGameToSpectate = $('#selectGameSpectate option').length;
                    var numberOfWaitingGame = $('#selectGameToJoin option').length;
                    var l = 1;
                    var m = 1;
                    for (var player in result.uniqueGames)
                    {
                        if (player !== login && !result.uniqueGames[player].isPrivate)
                        {
                            if (result.uniqueGames[player].state == "Phase 1" || result.uniqueGames[player].state == "Phase 2")
                            {
                                if (l < numberOfGameToSpectate + 1)
                                    $('#' + l + '-gameSpectateSelect').html(player).attr("value", player);
                                else
                                {
                                    $('#selectGameSpectate').append('<option id="' + l + '-gameSpectateSelect"' +
                                        ' value="' + player + '">' + player + '</option>');
                                    numberOfGameToSpectate++;
                                }
                                l++;
                            }
                            else if (result.uniqueGames[player].state == "En attente")
                            {
                                if (m < numberOfWaitingGame + 1)
                                    $('#' + m + '-gameWaitingSelect').html(player).attr("value", player);
                                else
                                {
                                    $('#selectGameToJoin').append('<option id="' + m + '-gameWaitingSelect"' +
                                        ' value="' + player + '">' + player + '</option>');
                                    numberOfWaitingGame++;
                                }
                                m++;
                            }
                        }
                    }
                    while($('#' + l + '-gameSpectateSelect').length)
                    {
                        $('#' + l + '-gameSpectateSelect').remove();
                        l++;
                    }
                    while($('#' + m + '-gameWaitingSelect').length)
                    {
                        $('#' + m + '-gameWaitingSelect').remove();
                        m++;
                    }
                },
                error: function(xhr)
                {
                    alert(JSON.parse(xhr.responseText).Message);
                }
            });
        }

    </script>

    <div class="container-fluid">
        <div class="row">
            <div class="col-md-5">
                <div class="jumbotron" id="Jumbotron">
                    <div id="player">
                        <h1 style="font-family:'BadaBoom BB';color:#09b6bc;font-weight:normal;font-size:42px;text-shadow: -1px 0 white, 0 1px white, 1px 0 white, 0 -1px white;">Bienvenue <strong><s:property value="#session.login"/></strong><img src="${pageContext.request.contextPath}/resources/images/User.png" id="userMenu" ></h1>
                    </div>
                </div>
            </div>
            <div class="col-md-2" id="onlinePlayers">
                <h3 style="font-family:'BadaBoom BB';color:#09b6bc;font-weight:normal;font-size:42px;text-shadow: -1px 0 white, 0 1px white, 1px 0 white, 0 -1px white;">Joueurs en ligne</h3>
                <br/>
                <select multiple id="selectPlayer" name="selectPlayer" class="form-control">
                    <s:set var="counter" value="0"/>
                    <s:iterator var="player" value="#session.playersOnline" status="k">
                        <s:if test="%{#player.key != #session.login}">
                            <s:set var="counter" value="%{#counter+1}"/>
                            <option id="<s:property value="%{#counter}"/>-playerSelect" value="<s:property value="%{#player.key}"/>"><s:property value="%{#player.key}"/></option>
                        </s:if>
                    </s:iterator>
                </select>
                <br/>
            </div>
            <div class="col-md-offset-1 col-md-2">
                <div class="well" id="well"> Vous serez déconnecté automatiquement en cas d'inactivité. Si vous voulez déconnecter directement, cliquez sur le bouton ci-dessous. </div>
                <s:form action="disconnection" theme="bootstrap">
                    <s:hidden name="login" value="%{#session.login}"/>
                    <s:submit cssClass="btn btn-primary deconnexion-button" value="Déconnexion"/>
                </s:form>
            </div>
        </div>

        <div class="row">

            <div class="col-md-offset-1 col-md-4" id="divJoinGame">
                <h3 style="font-family:'BadaBoom BB';color:#09b6bc;font-weight:normal;font-size:42px;text-shadow: -1px 0 white, 0 1px white, 1px 0 white, 0 -1px white;">Rejoindre une partie :</h3>
                <br/>
                <s:actionerror/>
                <s:form action="joinGame" theme="bootstrap">
                    <select id="selectGameToJoin" name="loginAdversary" class="form-control">
                        <s:set var="counterWaitingGame" value="0"/>
                        <s:iterator var="game" value="#session.uniqueGames" status="m">
                            <s:if test="%{#game.value.getIsPrivate() eq false}">
                                <s:if test="%{#game.key != #session.login && #game.value.getState() eq 'En attente'}">
                                    <s:set var="counterWaitingGame" value="%{#counterWaitingGame+1}"/>
                                    <option id="<s:property value="%{#counterWaitingGame}"/>-gameWaitingSelect" value="<s:property value="%{#game.key}"/>"><s:property value="%{#game.key}"/></option>
                                </s:if>
                            </s:if>
                        </s:iterator>
                    </select>
                    <s:hidden name="login" value="%{#session.login}"/>
                    <br/>
                    <s:submit cssClass="btn btn-primary center-block" value="Rejoindre" id="joinButton"/>
                </s:form>
            </div>

            <div class="col-md-offset-2 col-md-4" id="creerPartie">
                <s:form id="gameForm" action="game" theme="bootstrap" cssClass="form-vertical well creation-partie">
                    <h3 style="font-family:'BadaBoom BB';color:#09b6bc;font-weight:normal;font-size:42px;text-shadow: -1px 0 white, 0 1px white, 1px 0 white, 0 -1px white;">Creer une partie :</h3>
                    <s:radio class="gameType" id="gameType" name="gameType" list="{'Publique', 'Privée'}"/>
                    <s:hidden id ="createGame_login" name="login" value="%{#session.login}"/>
                    <input id="" type="submit" value="Créer" class="btn btn-primary center-block">
                </s:form>
            </div>

        </div>

        <div class="row">
            <div class="col-md-offset-1 col-md-4"  id="myInvits">
                <h3 style="font-family:'BadaBoom BB';color:#09b6bc;font-weight:normal;font-size:42px;text-shadow: -1px 0 white, 0 1px white, 1px 0 white, 0 -1px white;">Mes invitations :</h3>
                <br/>
                <s:actionerror/>
                <table id="invitations" style="border-collapse: separate; border-spacing: 10px;">
                    <s:iterator var="invitation" value="#session.invitations" status="i">
                        <tr id="<s:property value="%{#i.index}"/>-invitation">
                            <td><s:property value="%{#invitation.key}"/></td>
                            <td>
                                <s:form action="acceptInvitation" theme="bootstrap" cssClass="acceptInvitation">
                                    <s:hidden name="login" value="%{#session.login}"/>
                                    <s:hidden name="loginAdversary" value="%{#invitation.key}"/>
                                    <s:submit cssClass="btn btn-primary center-block" value="Accepter"/>
                                </s:form>
                            </td>
                            <td>
                                <s:form action="declineInvitation" theme="bootstrap" cssClass="declineInvitation">
                                    <s:hidden name="login" value="%{#session.login}"/>
                                    <s:hidden name="loginAdversary" value="%{#invitation.key}"/>
                                    <s:hidden name="position" value="%{#i.index}"/>
                                    <s:submit cssClass="btn btn-primary decline-button" value="Décliner"/>
                                </s:form>
                            </td>
                        </tr>
                    </s:iterator>
                </table>
            </div>
            <div class="col-md-offset-2 col-md-4" id="spectatorMode">
                <h3 style="font-family:'BadaBoom BB';color:#09b6bc;font-weight:normal;font-size:42px;text-shadow: -1px 0 white, 0 1px white, 1px 0 white, 0 -1px white;">Observer une partie :</h3>
                <br/>
                <s:form action="joinGame" theme="bootstrap">
                    <select id="selectGameSpectate" name="loginAdversary" class="form-control">
                        <s:set var="counterGameSpectate" value="0"/>
                        <s:iterator var="game" value="#session.uniqueGames" status="l">
                            <s:if test="%{#game.value.getIsPrivate() eq false}">
                                <s:if test="%{#game.key != #session.login && #game.value.getState() neq ('Terminée') && #game.value.getState() neq ('En attente')}">
                                    <s:set var="counterGameSpectate" value="%{#counterGameSpectate+1}"/>
                                    <option id="<s:property value="%{#counterGameSpectate}"/>-gameSpectateSelect" value="<s:property value="%{#game.key}"/>"><s:property value="%{#game.key}"/></option>
                                </s:if>
                            </s:if>
                        </s:iterator>
                    </select>
                    <s:hidden name="login" value="%{#session.login}"/>
                    <br/>
                    <s:submit id="buttonSpectate" cssClass="btn btn-primary center-block" value="Observer"/>
                </s:form>
            </div>
        </div>
        <br/>
        <br/>
    </div>
</body>
</html>
