package boopathi.app.mahendrahostel;

public class student_model {

    String fromtime;
    String fromdate;
    String enddate;
    String endtime;
    String requesttype;
    String outingreason;
    String wardenreason;
    String statuslist;



    public student_model(String fromdate, String fromtime, String enddate, String endtime, String requesttype, String outingreason, String wardenreason, String statuslist) {
        this.fromdate = fromdate;
        this.fromtime = fromtime;
        this.enddate = enddate;
        this.endtime = endtime;
        this.requesttype = requesttype;
        this.outingreason = outingreason;
        this.wardenreason = wardenreason;
        this.statuslist = statuslist;
    }

    public String getStatuslist() {
        return statuslist;
    }

    public void setStatuslist(String statuslist) {
        this.statuslist = statuslist;
    }

    public String getFromtime() {
        return fromtime;
    }

    public void setFromtime(String fromtime) {
        this.fromtime = fromtime;
    }

    public String getFromdate() {
        return fromdate;
    }

    public void setFromdate(String fromdate) {
        this.fromdate = fromdate;
    }

    public String getEnddate() {
        return enddate;
    }

    public void setEnddate(String enddate) {
        this.enddate = enddate;
    }

    public String getEndtime() {
        return endtime;
    }

    public void setEndtime(String endtime) {
        this.endtime = endtime;
    }

    public String getRequesttype() {
        return requesttype;
    }

    public void setRequesttype(String requesttype) {
        this.requesttype = requesttype;
    }

    public String getOutingreason() {
        return outingreason;
    }

    public void setOutingreason(String outingreason) {
        this.outingreason = outingreason;
    }

    public String getWardenreason() {
        return wardenreason;
    }

    public void setWardenreason(String wardenreason) {
        this.wardenreason = wardenreason;
    }
}
