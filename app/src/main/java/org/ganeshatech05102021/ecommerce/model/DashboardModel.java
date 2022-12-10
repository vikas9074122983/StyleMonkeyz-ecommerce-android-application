package org.ganeshatech05102021.ecommerce.model;

public class DashboardModel {
    private int pimg;
    private String pname,price;

    public DashboardModel() {
    }

    public DashboardModel(int pimg, String pname, String price) {
        this.pimg = pimg;
        this.pname = pname;
        this.price = price;
    }

    public int getPimg() {
        return pimg;
    }

    public void setPimg(int pimg) {
        this.pimg = pimg;
    }

    public String getPname() {
        return pname;
    }

    public void setPname(String pname) {
        this.pname = pname;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}
