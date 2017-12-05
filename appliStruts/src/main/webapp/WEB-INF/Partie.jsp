<%--
  Created by IntelliJ IDEA.
  User: root
  Date: 11/14/16
  Time: 12:10 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sb" uri="/struts-bootstrap-tags" %>
<%@ taglib prefix="sj" uri="/struts-jquery-tags" %>

<html>
<head>
    <title>Partie</title>
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
        var loginAdversary;

        var isObserver;

        var justArrived;

        var numberOfPieces;

        var type;

        var line;
        var column;

        var selectPiece;
        var selectTarget;

        var intervalRefreshTimer;

        var victory;


        $(document).ready(function()
        {
            $('.line').hide();
            $('.column').hide();

            login = $('#player')[0].innerHTML.split('>')[2].split('<')[0];
            justArrived = true;
            numberOfPieces = new Object();
            type = null;
            selectPiece = false;
            selectTarget = false;
            victory = false;

            refresh();

            $('td.piece').click(function()
            {
                type = $(this).find("p").html().split('(')[0];
            });

            $('td.piece').hover(
                    function()
                    {
                        $(this).find("p").show();
                    },
                    function()
                    {
                        $(this).find("p").hide();
                    }
            );

            $('td.square').hover(
                    function()
                    {
                        $(this).find("p").show();
                    },
                    function()
                    {
                        $(this).find("p").hide();
                    }
            );

            $('#piecesLost').hover(
                    function()
                    {
                        $(this).find("p").show();
                    },
                    function()
                    {
                        $(this).find("p").hide();
                    }
            );

            $('#piecesCaptured').hover(
                    function()
                    {
                        $(this).find("p").show();
                    },
                    function()
                    {
                        $(this).find("p").hide();
                    }
            );

            $('td.square').click(function()
            {

                if(type != null)
                {
                    line =  $(this).html().replace(/\s/g,'').split('>')[3].split('<')[0];
                    column = $(this).html().replace(/\s/g,'').split('>')[5].split('<')[0];

                    $.ajax({
                        url: "putPiece.action?login=" + login + "&type=" + type + "&line=" + line + "&column=" + column,
                        type: "POST",
                        contentType: "application/json: charset=utf-8",
                        dataType: "json",
                        success: function(result)
                        {
                            $('#' + line.toString() + column.toString() + '-square').prepend('<img src="${pageContext.request.contextPath}/resources/piece/' + result.colorPlayer + '/' + result.board[line][column].piece.type + '.png" style="height: 70px;" /><p hidden class="hoverPiece" style="position: absolute; color: white;">' + result.board[line][column].piece.type + '</p>');

                            var k = 0;
                            for(var typePiece in result.stock)
                            {
                                if(typePiece == type)
                                {
                                    if(result.stock[typePiece].length == 0)
                                        $('#' + k.toString() + '-piece').remove();
                                    else
                                        $('#' + k.toString() + '-piece').find("p").html(typePiece + '(' + result.stock[typePiece].length+')');
                                            break;
                                }
                                k++;
                            }

                            var readyForNextPhase = result.readyForNextPhase;
                            if(readyForNextPhase)
                                $('#remainingPieces').remove();
                        },
                        error: function()
                        {
                            type = null;
                            alert('Erreur!');
                        }
                    });
                }

                if(selectTarget)
                {
                    // Si la case ciblée contient une piece
                    if($(this).find("img").length)
                    {
                        var lineBis = $(this).html().replace(/\s/g,'').split('>')[6].split('<')[0];
                        var columnBis = $(this).html().replace(/\s/g,'').split('>')[8].split('<')[0];
                    } else
                    {
                        var lineBis = $(this).html().replace(/\s/g, '').split('>')[3].split('<')[0];
                        var columnBis = $(this).html().replace(/\s/g, '').split('>')[5].split('<')[0];
                    }

                    $.ajax({
                        url: "movePiece.action?login=" + login + "&line=" + line + "&column=" + column + "&lineBis=" + lineBis + "&columnBis=" + columnBis,
                        type: "POST",
                        contentType: "application/json: charset=utf-8",
                        dataType: "json",
                        success: function(result)
                        {
                            if(result.endGame)
                                victory = true;
                            else
                            {
                                if (!result.board[parseInt(result.line)][parseInt(result.column)].lake)
                                {
                                    $('#' + result.line + result.column + '-square')[0].innerHTML = '&nbsp;';
                                    if(isObserver)
                                        $('#playerTurn').html('<h1>Au tour du joueur 1.</h1>');
                                    else
                                        $('#playerTurn').html('<h1>Au tour de votre adversaire.</h1>');
                                    $('#timer').empty();
                                    timer = 15;
                                    clearInterval(intervalRefreshTimer);
                                }

                                if (!result.board[parseInt(result.lineBis)][parseInt(result.columnBis)].lake)
                                {
                                    if (!result.board[parseInt(result.lineBis)][parseInt(result.columnBis)].free)
                                        $('#' + result.lineBis + result.columnBis + '-square').html('<img src="${pageContext.request.contextPath}/resources/piece/' + result.board[parseInt(result.lineBis)][parseInt(result.columnBis)].piece.color + '/' + result.board[parseInt(result.lineBis)][parseInt(result.columnBis)].piece.type + '.png" style="height: 70px;" /><p hidden class="hoverPiece" style="position: absolute; color: white;">' + result.board[parseInt(result.lineBis)][parseInt(result.columnBis)].piece.type + '</p>');
                                    else
                                        $('#' + result.lineBis + result.columnBis + '-square')[0].innerHTML = '&nbsp;';
                                }

                                var numberOfPiecesLost = document.getElementById('piecesLost').rows.length;
                                var o = 0;
                                for(var typePiece in result.stock)
                                {
                                    if(result.stock[typePiece].length != 0)
                                    {
                                        if (o < numberOfPiecesLost)
                                            $('#' + o + '-piecesLost').html('<td><img src="${pageContext.request.contextPath}/resources/piece/' + result.colorPlayer + '/' + typePiece + '.png" style="height: 70px;" /> ' + result.stock[typePiece].length + '/' + numberOfPieces[typePiece] + '<p hidden class="hoverPiece" style="color: white;">' + typePiece + '</p></td>');
                                        else
                                        {
                                            $('#piecesLost').append('<tr id="' + numberOfPiecesLost + '-piecesLost" class="piecesLost"><td><img src="${pageContext.request.contextPath}/resources/piece/' + result.colorPlayer + '/' + typePiece + '.png" style="height: 70px;" /> '  + result.stock[typePiece].length + '/' + numberOfPieces[typePiece] + '<p hidden class="hoverPiece" style="color: white;">' + typePiece + '</p></td></tr>');
                                            numberOfPiecesLost++;
                                        }
                                        o++;
                                    }
                                }

                                var colorAdversary;

                                if(result.colorPlayer == 'steelblue')
                                    colorAdversary = 'indianred';
                                else
                                    colorAdversary = 'steelblue';

                                var numberOfPiecesCaptured = document.getElementById('piecesCaptured').rows.length;
                                var p = 0;
                                for(typePiece in result.stockAdversary)
                                {
                                    if(result.stockAdversary[typePiece].length != 0)
                                    {
                                        if (p < numberOfPiecesCaptured)
                                            $('#' + p + '-piecesCaptured').html('<td><img src="${pageContext.request.contextPath}/resources/piece/' + colorAdversary + '/' + typePiece + '.png" style="height: 70px;" /> ' + result.stockAdversary[typePiece].length + '/' + numberOfPieces[typePiece] + '<p hidden class="hoverPiece" style="color: white;">' + typePiece + '</p></td>');
                                        else
                                        {
                                            $('#piecesCaptured').append('<tr id="' + numberOfPiecesCaptured + '-piecesCaptured" class="piecesCaptured"><td><img src="${pageContext.request.contextPath}/resources/piece/' + colorAdversary + '/' + typePiece + '.png" style="height: 70px;" /> '  + result.stockAdversary[typePiece].length + '/' + numberOfPieces[typePiece] + '<p hidden class="hoverPiece" style="color: white;">' + typePiece + '</p></td></tr>');
                                            numberOfPiecesCaptured++;
                                        }
                                        p++;
                                    }
                                }
                            }

                        },
                        error: function()
                        {
                            alert('Ce n\'est pas à votre tour.');
                        }
                    });

                    line = null;
                    column = null;
                    lineBis = null;
                    columnBis = null;
                    selectTarget = false;
                    selectPiece = false;
                }
                if(selectPiece)
                {
                    line = $(this).html().replace(/\s/g,'').split('>')[6].split('<')[0];
                    column = $(this).html().replace(/\s/g,'').split('>')[8].split('<')[0];
                    selectPiece = false;
                    selectTarget = true;
                }
            });
            $('.sendInvitation').submit(function(e)
            {
                e.preventDefault();
                var data = $(this).serialize();

                $.ajax({
                    url:"sendInvitation.action?" + data,
                    type: "POST",
                    contentType: "application/json: charset=utf-8",
                    dataType: "json",
                    success: function()
                    {
                        $('#errorInvitePlayer').html('').removeClass('alert alert-danger actionError');
                        $('#successInvitePlayer').html('<br/>Invitation(s) envoyée(s).');

                        var timerInvitePlayer = 5;
                        setInterval(function refreshInvitePlayer()
                        {
                            timerInvitePlayer--;
                            if(timerInvitePlayer == 0)
                            {
                                $('#successInvitePlayer').html('');
                                clearInterval(refreshInvitePlayer);
                            }
                        }, 1000);
                    },
                    error: function()
                    {
                        $('#successInvitePlayer').html('');

                        $('#errorInvitePlayer').html('Veuillez sélectionner au moins un joueur à inviter.')
                                .attr('class', 'alert alert-danger actionError');
                    }
                });
            });
        });

        var intervalRefresh = setInterval('refresh()', 5000);
        var timer = 15;

        function refresh()
        {
            $.ajax({
                url: "refreshGame.action?login=" + login,
                type: "POST",
                contentType: "application/json: charset=utf-8",
                dataType: "json",
                success: function(result)
                {
                    if(result.isObserver)
                        isObserver = true;
                    else
                        isObserver = false;

                    var colorPlayer;
                    var colorAdversary;

                    if(!isObserver)
                    {
                        colorPlayer = result.colorPlayer;
                        if(colorPlayer == 'steelblue')
                            colorAdversary = 'indianred';
                        else
                            colorAdversary = 'steelblue';
                    }
                    else
                    {
                        colorPlayer = 'indianred';
                        colorAdversary = 'steelblue';
                    }

                    if(justArrived)
                    {
                        for(var typePiece in result.session.stock)
                        {
                            numberOfPieces[typePiece] = result.session.stock[typePiece].length;
                        }
                        justArrived = false;
                    }

                    /*
                    if(result.gameState == "Terminée")
                        $('#gameState').html('<h1>Terminee</h1>');
                    else
                        $('#gameState').html('<h1>' + result.gameState + '</h1>');
                     */

                    var font = 'BadaBoom BB';

                    if(result.gameState == "Phase 1")
                    {
                        $('#adversary').html('<h3 style="font-family:' + font + ';color:' + colorAdversary + ';font-size:32px;">Adversaire: <strong>' + result.loginAdversary + '</strong></h3>') ;

                        $('#formInvitePlayer').remove();

                        if ($('#loader').is(':visible'))
                            $('#loader').fadeToggle('fast');
                    }

                    if(!isObserver)
                    {
                        if(result.gameState != "En attente" && result.loginAdversary == "")
                        {
                            $('#adversary').html('<h3 style="font-family:' + font + ';color:' + colorAdversary + ';font-size:32px;">Adversaire: <strong>a quitte la partie.</strong></h3>');
                            victory = true;
                        }
                    }
                    else
                    {
                        if(result.loginPlayer1 == "")
                        {
                            $('#player1').html('<h3 style="font-family:' + font + ';color:steelblue;font-size:32px;">Joueur 1: <strong>a quitte la partie.</strong></h3>');
                            victory = true;
                        }
                        else if(result.loginPlayer2 == "")
                            $('#player2').html('<h3 style="font-family:' + font + ';color:indianred;font-size:32px;">Joueur 2: <strong>a quitte la partie.</strong></h3>');
                    }

                    if(result.gameState == "Terminée")
                    {
                        if(result.loginPlayer1 != "" && result.loginPlayer2 != "" && isObserver)
                            victory = result.victory;

                        if(victory)
                        {
                            if(isObserver)
                                $('#result').html('<h3 style="font-family:' + font + ';font-size:70px;color:indianred"><strong>Victoire du joueur 2!</strong>');
                            else
                                $('#result').html('<h3 style="font-family:' + font + ';font-size:70px;color:' + colorPlayer +'"><strong>Victoire!</strong>');
                        }
                        else
                        {
                            if(isObserver)
                                $('#result').html('<h3 style="font-family:' + font + ';font-size:70px;color:steelblue"><strong>Victoire du joueur 1!</strong>');
                            else
                                $('#result').html('<h3 style="font-family:' + font + ';font-size:70px;color:' + colorPlayer + '"><strong>Defaite!</strong>');
                        }

                        $('#turn').remove();

                        clearInterval(intervalRefresh);
                    }


                    if(result.isPrivate && result.gameState == "En attente")
                    {
                        var numberOfPlayersOnline = $('#selectPlayer option').length;
                        var k = 1;
                        for (var player in result.playersOnline)
                        {
                            if (player != login)
                            {
                                if (k < numberOfPlayersOnline + 1)
                                    $('#' + k + '-playerSelect').html(player).attr('value', player);
                                else
                                {
                                    $('#selectPlayer').append('<option id="' + k + '-playerSelect"' + ' value="' + player + '">' + player + '</option>');
                                    numberOfPlayersOnline++;
                                }
                                k++;
                            }
                        }
                        while ($('#' + k + '-playerSelect').length)
                        {
                            $('#' + k + '-playerSelect').remove();
                            k++;
                        }
                    }

                    var m = 0;
                    for(var square in result.board)
                    {
                        for(var n = 0; n < 10; n++)
                        {
                            if (result.board[m][n].free)
                                $('#' + m.toString() + n.toString() + '-square')[0].innerHTML = '&nbsp;';
                            else
                            {
                                if(!result.board[m][n].lake)
                                {
                                    if(result.gameState == "Terminée")
                                        $('#' + m.toString() + n.toString() + '-square').html('<img src="${pageContext.request.contextPath}/resources/piece/' + result.board[m][n].piece.color + '/' + result.board[m][n].piece.type + '.png" style="height: 70px;" /><p hidden class="hoverPiece" style="position: absolute; color: white;">' + result.board[m][n].piece.type + '</p>');
                                    else
                                    {
                                        if(isObserver || result.board[m][n].piece.color != colorPlayer)
                                            $('#' + m.toString() + n.toString() + '-square').html('<img src="${pageContext.request.contextPath}/resources/piece/' + result.board[m][n].piece.color + '/hidden.png" style="height: 70px;" /><div></div>');
                                        else
                                            $('#' + m.toString() + n.toString() + '-square').html('<img src="${pageContext.request.contextPath}/resources/piece/' + result.board[m][n].piece.color + '/' + result.board[m][n].piece.type + '.png" style="height: 70px;" /><p hidden class="hoverPiece" style="position: absolute; color: white;">' + result.board[m][n].piece.type + '</p>');
                                    }
                                }
                            }
                        }
                        m++;
                    }

                    if(result.gameState == "Phase 2")
                    {
                        type = null;
                        selectPiece = true;
                        if(result.canMovePiece)
                        {
                            if(isObserver)
                                $('#playerTurn').html('<h1>Au tour du joueur 2.</h1>');
                            else
                            {
                                $('#playerTurn').html('<h1>A vous de jouer: </h1>');
                                $('#timer').html('<h3>' + timer + ' seconde(s) restante(s).</h3>');
                                if (timer !== 0)
                                {
                                    timer--;
                                    var cptRefreshForTimer = 1;
                                    intervalRefreshTimer = setInterval(function () {
                                        $('#timer').html('<h3>' + timer + ' seconde(s) restante(s).</h3>');
                                        timer--;
                                        cptRefreshForTimer++;
                                        if (cptRefreshForTimer == 5)
                                            clearInterval(intervalRefreshTimer);
                                    }, 1000);
                                } else
                                {
                                    $.ajax({
                                        url: "skipTurn.action?login=" + login,
                                        type: "POST",
                                        contentType: "application/json: charset=utf-8",
                                        dataType: "json",
                                        success: function () {
                                            timer = 15;
                                            $('#timer').empty();
                                            //alert('Votre tour a été passé suite à votre inactivité.');
                                            if (isObserver)
                                                $('#playerTurn').html('<h1>Au tour du joueur 1.</h1>');
                                            else
                                                $('#playerTurn').html('<h1>Au tour de votre adversaire.</h1>');
                                            clearInterval(intervalRefreshTimer);
                                        },
                                        error: function () {
                                        }
                                    });
                                }
                            }
                        } else
                        {
                            $('#timer').html('');
                            timer = 15;
                            if(isObserver)
                                $('#playerTurn').html('<h1>Au tour du joueur 1.</h1>');

                            else
                                $('#playerTurn').html('<h1>Au tour de votre adversaire.</h1>');
                        }

                        $('#columnPiecesCaptured').css('visibility', 'visible');
                        $('#columnPiecesLost').css('visibility', 'visible');

                        var numberOfPiecesLost = document.getElementById('piecesLost').rows.length;
                        var o = 0;
                        for(typePiece in result.stock)
                        {
                            if(result.stock[typePiece].length != 0)
                            {
                                if (o < numberOfPiecesLost)
                                    $('#' + o + '-piecesLost').html('<td><img src="${pageContext.request.contextPath}/resources/piece/' + colorPlayer + '/' + typePiece + '.png" style="height: 70px;" /> ' + result.stock[typePiece].length + '/' + numberOfPieces[typePiece] + '<p hidden class="hoverPiece" style="color: white;">' + typePiece + '</p></td>');
                                else
                                {
                                    $('#piecesLost').append('<tr id="' + numberOfPiecesLost + '-piecesLost" class="piecesLost"><td><img src="${pageContext.request.contextPath}/resources/piece/' + colorPlayer + '/' + typePiece + '.png" style="height: 70px;" /> '  + result.stock[typePiece].length + '/' + numberOfPieces[typePiece] + '<p hidden class="hoverPiece" style="color: white;">' + typePiece + '</p></td></tr>');
                                    numberOfPiecesLost++;
                                }
                                o++;
                            }
                        }

                        var numberOfPiecesCaptured = document.getElementById('piecesCaptured').rows.length;
                        var p = 0;
                        for(typePiece in result.stockAdversary)
                        {
                            if(result.stockAdversary[typePiece].length != 0)
                            {
                                if (p < numberOfPiecesCaptured)
                                    $('#' + p + '-piecesCaptured').html('<td><img src="${pageContext.request.contextPath}/resources/piece/' + colorAdversary + '/' + typePiece + '.png" style="height: 70px;" /> ' + result.stockAdversary[typePiece].length + '/' + numberOfPieces[typePiece] + '<p hidden class="hoverPiece" style="color: white;">' + typePiece + '</p></td>');
                                else
                                {
                                    $('#piecesCaptured').append('<tr id="' + numberOfPiecesCaptured + '-piecesCaptured" class="piecesCaptured"><td><img src="${pageContext.request.contextPath}/resources/piece/' + colorAdversary + '/' + typePiece + '.png" style="height: 70px;" /> '  + result.stockAdversary[typePiece].length + '/' + numberOfPieces[typePiece] + '<p hidden class="hoverPiece" style="color: white;">' + typePiece + '</p></td></tr>');
                                    numberOfPiecesCaptured++;
                                }
                                p++;
                            }
                        }
                    }
                },
                error: function()
                {
                    alert("Erreur!");
                }
            });
        }

    </script>

    <s:hidden var="colorPlayer" value="#session.colorPlayer"/>
    <s:hidden var="colorAdversary" value="#session.colorAdversary"/>

    <div class="container-fluid">
        <div class="row" style="visibility: hidden;">
            <div class="col-md-offset-3 col-md-6">
                <div id="gameState" style="margin-top: 10px; background-color: rgba(52, 73, 94,0.40);border-radius:15px;color: #FFFFFF;font-family: 'BadaBoom BB';font-size: larger;font-style: inherit;text-align: center;">
                    <h1><s:property value="#session.game.state"/></h1>
                </div>
            </div>
        </div>
        <div class="row">
            <div class="col-md-3">
                <s:if test="%{#session.isObserver}">
                    <div id="player" style="visibility: hidden;">
                        <h3>Joueur : <strong><s:property value="#session.login"/></strong></h3>
                    </div>
                    <div id="player1">
                        <h3 style="font-family:'BadaBoom BB';color: steelblue;font-size:32px;text-shadow: -1px 0 white, 0 1px white, 1px 0 white, 0 -1px white;">Joueur 1 : <s:property value="#session.loginPlayer1"/></h3>
                    </div>
                    <div id="player2" style="position: relative">
                        <h3 style="font-family:'BadaBoom BB';color: indianred;font-size:32px;text-shadow: -1px 0 white, 0 1px white, 1px 0 white, 0 -1px white;">Joueur 2 : <s:property value="#session.loginPlayer2"/></h3>
                    </div>
                </s:if>
                <s:else>
                    <div id="player">
                        <h3 style="font-family:'BadaBoom BB';color: ${colorPlayer};font-size:32px;text-shadow: -1px 0 white, 0 1px white, 1px 0 white, 0 -1px white;">Joueur : <strong><s:property value="#session.login"/></strong></h3>
                    </div>
                    <div id="adversary" style="position: relative">
                        <h3 style="font-family:'BadaBoom BB';color: ${colorAdversary};font-size:32px;text-shadow: -1px 0 white, 0 1px white, 1px 0 white, 0 -1px white;">Adversaire : <s:property value="#session.loginAdversary"/></h3>
                        <s:if test="%{#session.loginAdversary eq null}">
                            <div id="loader" class="loader" style="margin-left:180px;"></div>
                        </s:if>
                    </div>
                </s:else>
            </div>
            <s:if test="%{(#session.isPrivate eq true)}">
                <div class="col-md-offset-7 col-md-2" id="formInvitePlayer" style="right: 5%;background-color: rgba(52, 73, 94,0.40);border-radius:15px;">
                    <h3 style="font-family:'BadaBoom BB';color: #09b6bc;font-size:32px;text-shadow: -1px 0 white, 0 1px white, 1px 0 white, 0 -1px white;">Inviter un joueur: </h3>
                    <br/>
                    <s:form action="sendInvitation" theme="bootstrap" cssClass="sendInvitation" >
                        <select multiple id="selectPlayer" name="selectPlayer" class="form-control">
                            <s:set var="counter" value="0"/>
                            <s:iterator var="player" value="#session.playersOnline" status="k">
                                <s:if test="%{#player.key != #session.login}">
                                    <s:set var="counter" value="%{#counter+1}"/>
                                    <option id="<s:property value="%{#counter}"/>-playerSelect" value="<s:property value="%{#player.key}"/>"><s:property value="%{#player.key}"/></option>
                                </s:if>
                            </s:iterator>
                        </select>
                        <s:hidden name="login" value="%{#session.login}"/>
                        <br/>
                        <div id="errorInvitePlayer" style="text-align:center;font-size:18px;color: black;">
                        </div>
                        <s:submit cssClass="btn btn-primary center-block" value="Inviter"/>
                        <div id="successInvitePlayer" style="text-align:center;font-size:18px;color:#FFFFFF;">
                        </div>
                    </s:form>
                </div>
            </s:if>
        </div>
        <br/>
        <br/>
        <div class="row" id="turn">
            <div class="col-md-offset-4 col-md-4">
                <div id="playerTurn"></div><div id="timer" style="position: absolute;bottom: 4px;left: 250px"></div>
            </div>
        </div>
        <br/>
        <br/>
        <div class="row">
            <div class="col-md-offset-3 col-md-6">
                <div id="result">
                </div>
            </div>
        </div>
        <br/>
        <br/>
        <div class="row">
            <div id="columnPiecesCaptured" class="col-md-2" style="background-color:rgba(52, 73, 94,0.40);border-radius:15px;padding-bottom:10px;text-align:center;visibility:hidden">
                <s:if test="%{!#session.isObserver}">
                  <h3 style="color: #FFFFFF;">Piece(s) capture(s)</h3>
                </s:if>
                <s:else>
                    <h3 style="color: #FFFFFF;">Piece(s) perdu(s) joueur 1</h3>
                </s:else>
                 <br/>
                <table id="piecesCaptured">
                </table>
            </div>
            <div class="col-md-offset-1 col-md-6" style="background-color: rgba(52, 73, 94,0.40);border-radius:15px;padding-top:30px;padding-bottom:30px">
                <table background="${pageContext.request.contextPath}/resources/images/board.jpg" rules="all" style="table-layout: fixed; width:710px; background-size: 710px 710px; margin-left: auto; margin-right: auto" >
                    <s:iterator var="line" value="#session.board" status="i">
                        <tr style="height:71px;">
                            <s:iterator var="column" value="#line" status="j">
                                <td class="square" align="center">
                                    <div id="<s:property value="%{#i.index}"/><s:property value="%{#j.index}"/>-square">
                                        <s:if test="%{#column.piece neq null}">
                                            <s:if test="%{#session.isObserver}">
                                                <img src="${pageContext.request.contextPath}/resources/piece/<s:property value="#column.piece.color"/>/hidden.png" style="height: 70px;" />
                                            </s:if>
                                            <s:else>
                                                <img src="${pageContext.request.contextPath}/resources/piece/<s:property value="#column.piece.color"/>/<s:property value="#column.piece"/>.png" style="height: 70px;" />
                                                <p hidden class="hoverPiece" style="position: absolute; color: white;"><s:property value="#column.piece"/></p>
                                            </s:else>
                                        </s:if>
                                        <s:else>
                                            &nbsp;
                                        </s:else>
                                    </div>
                                    <div class="line">
                                        <s:property value="%{#i.index}"/>
                                    </div>
                                    <div class="column">
                                        <s:property value="%{#j.index}"/>
                                    </div>
                                </td>
                            </s:iterator>
                        </tr>
                    </s:iterator>
                </table>
            </div>
            <div id="columnPiecesLost" class="col-md-offset-1 col-md-2" style="background-color:rgba(52, 73, 94,0.40);border-radius:15px;padding-bottom:10px;text-align:center;visibility:hidden">
                <s:if test="%{!#session.isObserver}">
                    <h3 style="color: #FFFFFF;">Piece(s) perdue(s)</h3>
                </s:if>
                <s:else>
                    <h3 style="color: #FFFFFF;">Piece(s) perdu(s) joueur 2</h3>
                </s:else>
                <br/>
                <table id="piecesLost">
                </table>
            </div>
        </div>
        <br/>
        <br/>
        <s:if test="%{!#session.isObserver}">
            <div class="row" id="remainingPieces">
                <div class="col-md-offset-3 col-md-6" style="background-color: rgba(52, 73, 94,0.40);border-radius:15px;padding-bottom: 50px;padding-top: 10px;">
                    <h3 style="color: white">Piece(s) a placer:</h3>
                    <br/>
                    <table style="border-collapse: separate; border-spacing: 10px; margin-left: auto; margin-right: auto">
                        <tr>
                            <s:iterator var="piece" value="#session.stock" status="k">
                                <td id="<s:property value="%{#k.index}"/>-piece" class="piece">
                                    <img src="${pageContext.request.contextPath}/resources/piece/${colorPlayer}/${piece.key}.png" style="height: 70px;" />
                                    <p hidden class="hoverPiece" style="position: absolute; color: white;"><s:property value="%{#piece.key}"/>(<s:property value="%{#piece.value.size}"/>)</p>
                                </td>
                            </s:iterator>
                        </tr>
                    </table>
                </div>
            </div>
            <br/>
            <br/>
        </s:if>
        <div class="row">
            <div class="cold-md-6">
                <div>
                    <s:form action="leaveGame" theme="bootstrap" cssStyle="text-align: center">
                        <s:hidden name="login" value="%{#session.login}"/>
                        <s:submit value="Quitter partie" cssClass="btn btn-primary decline-button"/>
                    </s:form>
                    <br/>
                </div>
            </div>
        </div>
    </div>
</body>
</html>
