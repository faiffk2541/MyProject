/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jpa.model;

import java.io.Serializable;
import java.math.BigDecimal;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Ploy
 */
@Entity
@Table(name = "ORDERDETAIL")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Orderdetail.findAll", query = "SELECT o FROM Orderdetail o")
    , @NamedQuery(name = "Orderdetail.findByOrdernumber", query = "SELECT o FROM Orderdetail o WHERE o.orderdetailPK.ordernumber = :ordernumber")
    , @NamedQuery(name = "Orderdetail.findByProductcode", query = "SELECT o FROM Orderdetail o WHERE o.orderdetailPK.productcode = :productcode")
    , @NamedQuery(name = "Orderdetail.findByQuantityordered", query = "SELECT o FROM Orderdetail o WHERE o.quantityordered = :quantityordered")
    , @NamedQuery(name = "Orderdetail.findByPriceeach", query = "SELECT o FROM Orderdetail o WHERE o.priceeach = :priceeach")
    , @NamedQuery(name = "Orderdetail.findByOrderlinenumber", query = "SELECT o FROM Orderdetail o WHERE o.orderlinenumber = :orderlinenumber")})
public class Orderdetail implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected OrderdetailPK orderdetailPK;
    @Basic(optional = false)
    @NotNull
    @Column(name = "QUANTITYORDERED")
    private int quantityordered;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Basic(optional = false)
    @NotNull
    @Column(name = "PRICEEACH")
    private BigDecimal priceeach;
    @Basic(optional = false)
    @NotNull
    @Column(name = "ORDERLINENUMBER")
    private short orderlinenumber;
    @JoinColumn(name = "ORDERNUMBER", referencedColumnName = "ORDERNUMBER", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Orders orders;
    @JoinColumn(name = "PRODUCTCODE", referencedColumnName = "PRODUCTCODE", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Product product;

    public Orderdetail() {
    }

    public Orderdetail(OrderdetailPK orderdetailPK) {
        this.orderdetailPK = orderdetailPK;
    }

    public Orderdetail(OrderdetailPK orderdetailPK, int quantityordered, BigDecimal priceeach, short orderlinenumber) {
        this.orderdetailPK = orderdetailPK;
        this.quantityordered = quantityordered;
        this.priceeach = priceeach;
        this.orderlinenumber = orderlinenumber;
    }

    public Orderdetail(int ordernumber, String productcode) {
        this.orderdetailPK = new OrderdetailPK(ordernumber, productcode);
    }

    public OrderdetailPK getOrderdetailPK() {
        return orderdetailPK;
    }

    public void setOrderdetailPK(OrderdetailPK orderdetailPK) {
        this.orderdetailPK = orderdetailPK;
    }

    public int getQuantityordered() {
        return quantityordered;
    }

    public void setQuantityordered(int quantityordered) {
        this.quantityordered = quantityordered;
    }

    public BigDecimal getPriceeach() {
        return priceeach;
    }

    public void setPriceeach(BigDecimal priceeach) {
        this.priceeach = priceeach;
    }

    public short getOrderlinenumber() {
        return orderlinenumber;
    }

    public void setOrderlinenumber(short orderlinenumber) {
        this.orderlinenumber = orderlinenumber;
    }

    public Orders getOrders() {
        return orders;
    }

    public void setOrders(Orders orders) {
        this.orders = orders;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (orderdetailPK != null ? orderdetailPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Orderdetail)) {
            return false;
        }
        Orderdetail other = (Orderdetail) object;
        if ((this.orderdetailPK == null && other.orderdetailPK != null) || (this.orderdetailPK != null && !this.orderdetailPK.equals(other.orderdetailPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "jpa.model.Orderdetail[ orderdetailPK=" + orderdetailPK + " ]";
    }
    
}
