<%@page import="org.springframework.context.ApplicationContext" %>
<%@page import="org.springframework.web.context.support.WebApplicationContextUtils" %>
<%@page import="java.util.Arrays" %>
<html>
<head>
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.7.2/jquery.min.js"></script>
    <link rel="stylesheet" type="text/css" href="/whp/resources/styles/bootstrap.css/"/>
    <link rel="stylesheet" type="text/css" href="/whp/resources/styles/standard.css"/>
</head>
<body>
<span id="statusMessage" style="font-size: medium; font-weight: bold; color: blue;"></span>
<br/>
<br/>

<div class="container">
    <div class="pull-right"><a href="/whp/emulator/">home</a></div>

    <form name="fakeTimeSubmit">
        <div class="row-fluid">
            <span style="vertical-align:top" class="pull-left span3">Date</span>
            <input id="date" class="span" name="date" type="text" value=""/>
        </div>
        <div class="row-fluid">
            <span class="pull-left span3" style="vertical-align:top">Hour</span>
            <input id="hour" name="hour" class="span2" type="text" value=""/>
        </div>
        <div class="row-fluid">
            <span class="pull-left span3" style="vertical-align:top">Minutes</span>
            <input id="minute" class="span2" type="text" value=""/>
        </div>
        <input type="button" id="post-button" value="Submit"/>
    </form>
    <script type="text/javascript">
            $('#post-button').click(function () {
            var host = window.location.host;
            var urlString = "/motech-delivery-tools/datetime/update?date=" + $("#date").val() + "&hour=" + $("#hour").val() + "&minute=" + $("#minute").val();
            $.ajax({
                type:'GET',
                url:"http://" + host + urlString,
                contentType:"application/xml; charset=utf-8",
                success:function (data, textStatus, jqXHR) {
                    alert("'hello");
                    $('#statusMessage').html("Status of request: SUCCESS");
                },
                error:function (xhr, status, error) {
                    $('#statusMessage').html("Status of request: FAILURE. Reason: " + error);
                }
            });

        });
    </script>
</div>
</body>
</html>