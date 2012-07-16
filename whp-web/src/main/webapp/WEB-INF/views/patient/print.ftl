<#import "/spring.ftl" as spring />
<!DOCTYPE html>
<html>
<head>
    <title>Treatment Card</title>
<#include "../layout/scripts.ftl"/>
</head>
<body>
<div id="navibar" class="navbar-fixed-top">
    <a href="<@spring.url '/' />">
        <img class="pull-right" src="<@spring.url '/resources-${applicationVersion}/images/whplogo.png'/>"/>
    </a>
</div>

<div class="container printable-version print-version-line-height">
<div class="title">Treatment Card</div>
<div class="row-fluid" id="mainContent">
<div class="span4">
    <label class="tc-label">State</label> <label class="tc-value">${patient.addressState}</label>
</div>
<div class="span4">
    <label class="tc-label">City/District with code</label>
    <label class="tc-value">${patient.addressDistrict}&nbsp;&nbsp;&nbsp;&nbsp;</label>
</div>
<div class="span3">
    <label class="tc-label">TB Unit with code</label>
    <label class="tc-value">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</label>
</div>
<div class="tc-div">
    <label class="tc-label">Name</label><label class="tc-value">${patient.firstName} ${patient.lastName}</label>

    <div>
        <label class="tc-label">Sex</label><label class="tc-value">${patient.gender}</label>
        <label class="tc-label">Occupation</label><label class="tc-value">
        &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</label>
    </div>
    <div>
        <label class="tc-label">Address</label><label class="tc-value">${patient.address}</label>
    </div>
    <div>
        <label class="tc-label">Phone Number</label><label class="tc-value">${patient.phoneNumber}</label>
    </div>
    <div>
        <label class="tc-label">Address and Phone Number of Contact Person</label><label class="tc-value"><br/></label>
        <br/>
    </div>
    <div>
        <label class="tc-label">Initial home visit by</label><label class="tc-value width-60px"></label>
        <label class="tc-label">Date</label><label class="tc-value">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</label>
    </div>
    <div>
        <label class="tc-label">Disease Classification</label><label
            class="tc-value">${patient.diseaseClass}</label>
    </div>
    <div>
        <label class="tc-label">Type of Patient</label><label class="tc-value">${patient.patientType}</label>
    </div>
</div>
<div class="left-spaced tc-div">
    <div>
        <label class="tc-label">Patient TB Id</label><label class="tc-value">${patient.tbId}</label>
        <label class="tc-label">Patient TB No/Year</label><label
            class="tc-value">${patient.tbRegistrationNumber!}&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</label>
    </div>
    <div>
        <label class="tc-label">PHI</label>
        <label class="tc-value">${patient.phi!}</label>
    </div>
    <div>
        <label class="tc-label">Name and designation of DOT provider & Tel No</label>
        <label class=""><br/></label><br/>
    </div>
    <div>
        <label class="tc-label">DOT centre</label>
        <label class="tc-value">&nbsp;&nbsp;&nbsp;&nbsp;</label>
    </div>
    <div><label class="tc-label">Signature of MO with date</label>
        <label class="tc-value">&nbsp;&nbsp;&nbsp;</label>
    </div>
</div>
<div class="pull-right">
    <table class="table table-bordered sharp fixed text-center">
        <tr>
            <th rowspan="2">Sample Instance</th>
            <th rowspan="2">DMC</th>
            <th rowspan="2">Lab No.</th>
            <th colspan="4">Smear Test Results</th>
            <th rowspan="2">Weight</th>
        </tr>
        <tr>
            <th>Date 1</th>
            <th>Result 1</th>
            <th>Date 2</th>
            <th>Result 2</th>
        </tr>
    <#list patient.testResults as testResult>
        <tr>
            <td>${testResult.sampleInstance}</td>
            <td></td>
            <td></td>
            <td>${testResult.smearTestDate1}</td>
            <td>${testResult.smearTestResult1}</td>
            <td>${testResult.smearTestDate2}</td>
            <td>${testResult.smearTestResult2}</td>
            <td>${testResult.weight}</td>
        </tr>
    </#list>
    </table>
</div>
<div id="treatmentCard">
<#include "../treatmentcard/print.ftl">
</div>
<div class="overflow-hidden">
    <label class="tc-label span6">Treatment outcome with date</label>
    <label class="tc-label span6">Signature of MO with date</label>
</div>
<div class="x-ray-and-remarks">
    <div class="x-ray">
        Details of X ray / EP tests
    </div>
    <label class="span2 tc-label">
        Remarks
    </label>
</div>
<div class="actions-for-missed-doses">
    <h4>Retrieval Actions for Missed Doses</h4>
    <table class="table table-bordered sharp fixed text-center line-height-normal">
        <tr>
            <th>Date</th>
            <th>By whom</th>
            <th>Reason for missed doses</th>
            <th>Outcome of retrieval action</th>
        </tr>
        <tr>
            <td></td>
            <td></td>
            <td></td>
            <td></td>
        </tr>
        <tr>
            <td></td>
            <td></td>
            <td></td>
            <td></td>
        </tr>
        <tr>
            <td></td>
            <td></td>
            <td></td>
            <td></td>
        </tr>
        <tr>
            <td></td>
            <td></td>
            <td></td>
            <td></td>
        </tr>
        <tr>
            <td></td>
            <td></td>
            <td></td>
            <td></td>
        </tr>
        <tr>
            <td></td>
            <td></td>
            <td></td>
            <td></td>
        </tr>
        <tr>
            <td></td>
            <td></td>
            <td></td>
            <td></td>
        </tr>
    </table>
</div>
<div class="chemoprophylaxis">
    <h4 class="text-center">Household Contacts </h4>
    <h4 class="text-center">(Children &lt; 6 yrs)</h4>
    <table class="table table-bordered sharp fixed text-center line-height-normal">
        <tr>
            <th>No.</th>
            <th>Chemoprophylaxis</th>
        </tr>
        <tr>
            <td></td>
            <td></td>
        </tr>
        <tr>
            <td></td>
            <td></td>
        </tr>
        <tr>
            <td></td>
            <td></td>
        </tr>
        <tr>
            <td></td>
            <td></td>
        </tr>
        <tr>
            <td></td>
            <td></td>
        </tr>
    </table>
</div>
<div class="additional-treatments">
    <h4 class="text-center">Addtional Treatments</h4>

    <div>
        <label class="tc-label">HIV status</label>
        <div class="empty-bordered"></div>
        <label class="tc-value">Unknown</label>
        <div class="empty-bordered"></div><label class="tc-value">Pos</label>
        <div class="empty-bordered"></div><label class="tc-value">Neg</label>
        <label class="tc-value"> (date)</label>
    </div>
    <div>
        <label class="tc-label">CPT delivered on (date)</label>
        <label class="tc-value width-30px">(1)</label>
        <label class="tc-value width-30px">(2)</label>
        <label class="tc-value width-30px">(3)</label>
        <label class="tc-value width-30px">(4)</label>
        <label class="tc-value width-30px">(5)</label>
    </div>
    <div>
        <label class="tc-label">Pt referred to ART centre (date)</label>

    </div>
    <div>
        <label class="tc-label">Initiated on ART</label>
        <div class="empty-bordered"></div>
        <label class="tc-value">No</label>
        <div class="empty-bordered"></div>
        <label class="tc-value">Yes</label>
    </div>

</div>
</div>

</div>
</body>
<script type="text/javascript">

            window.print();
</script>

</html>
