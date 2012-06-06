<#import "/spring.ftl" as spring />
<#include "../user/phaseInfo.ftl">
<#import "../layout/default.ftl" as layout>
<@layout.defaultLayout "Patient Dashboard">
    <#if messages?exists && (messages?size>0)>
        <div class="dateUpdated-message-alert row alert alert-info fade in">
            <button class="close" data-dismiss="alert">&times;</button>
            <#list messages as message>
                ${message}
                <#break/>
            </#list>
            <table id="messageList" class="table table-bordered table-condensed sharp">
                <thead>
                <tr>
                    <th>Phase Information</th>
                </tr>
                <tbody>
                    <#list messages as message>
                        <#if message_index != 0>
                            <tr>
                                <td>${message}</td>
                                <br/>
                            </tr>
                        </#if>
                        <#assign message=""/>
                    </#list>
                </tbody>
            </table>
        </div>
    </#if>
    <div class="well">
        <a id="setDateLink" data-toggle="modal" href="#setDatesModal" class="brand pull-left">Adjust Phase Start Dates</a>
    </div>
    <@phaseInfo/>
    <script type="text/javascript">
        $('#ipDatePicker').datepicker();
        $('#eipDatePicker').datepicker();
        $('#cpDatePicker').datepicker();
        $("#clearFields").click(function() {
            $('#ipDatePicker').val('');
            $('#eipDatePicker').val('');
            $('#cpDatePicker').val('');
            event.preventDefault();
        });
        createAutoClosingAlert(".dateUpdated-message-alert", 5000)
    </script>
</@layout.defaultLayout>