/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jpa.Controller;

import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import jpa.model.Product;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;
import jpa.Controller.exceptions.IllegalOrphanException;
import jpa.Controller.exceptions.NonexistentEntityException;
import jpa.Controller.exceptions.PreexistingEntityException;
import jpa.Controller.exceptions.RollbackFailureException;
import jpa.model.Productline;

/**
 *
 * @author Ploy
 */
public class ProductlineJpaController implements Serializable {

    public ProductlineJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Productline productline) throws PreexistingEntityException, RollbackFailureException, Exception {
        if (productline.getProductList() == null) {
            productline.setProductList(new ArrayList<Product>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            List<Product> attachedProductList = new ArrayList<Product>();
            for (Product productListProductToAttach : productline.getProductList()) {
                productListProductToAttach = em.getReference(productListProductToAttach.getClass(), productListProductToAttach.getProductcode());
                attachedProductList.add(productListProductToAttach);
            }
            productline.setProductList(attachedProductList);
            em.persist(productline);
            for (Product productListProduct : productline.getProductList()) {
                Productline oldProductlineOfProductListProduct = productListProduct.getProductline();
                productListProduct.setProductline(productline);
                productListProduct = em.merge(productListProduct);
                if (oldProductlineOfProductListProduct != null) {
                    oldProductlineOfProductListProduct.getProductList().remove(productListProduct);
                    oldProductlineOfProductListProduct = em.merge(oldProductlineOfProductListProduct);
                }
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            if (findProductline(productline.getProductline()) != null) {
                throw new PreexistingEntityException("Productline " + productline + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Productline productline) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Productline persistentProductline = em.find(Productline.class, productline.getProductline());
            List<Product> productListOld = persistentProductline.getProductList();
            List<Product> productListNew = productline.getProductList();
            List<String> illegalOrphanMessages = null;
            for (Product productListOldProduct : productListOld) {
                if (!productListNew.contains(productListOldProduct)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Product " + productListOldProduct + " since its productline field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<Product> attachedProductListNew = new ArrayList<Product>();
            for (Product productListNewProductToAttach : productListNew) {
                productListNewProductToAttach = em.getReference(productListNewProductToAttach.getClass(), productListNewProductToAttach.getProductcode());
                attachedProductListNew.add(productListNewProductToAttach);
            }
            productListNew = attachedProductListNew;
            productline.setProductList(productListNew);
            productline = em.merge(productline);
            for (Product productListNewProduct : productListNew) {
                if (!productListOld.contains(productListNewProduct)) {
                    Productline oldProductlineOfProductListNewProduct = productListNewProduct.getProductline();
                    productListNewProduct.setProductline(productline);
                    productListNewProduct = em.merge(productListNewProduct);
                    if (oldProductlineOfProductListNewProduct != null && !oldProductlineOfProductListNewProduct.equals(productline)) {
                        oldProductlineOfProductListNewProduct.getProductList().remove(productListNewProduct);
                        oldProductlineOfProductListNewProduct = em.merge(oldProductlineOfProductListNewProduct);
                    }
                }
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                String id = productline.getProductline();
                if (findProductline(id) == null) {
                    throw new NonexistentEntityException("The productline with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(String id) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Productline productline;
            try {
                productline = em.getReference(Productline.class, id);
                productline.getProductline();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The productline with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Product> productListOrphanCheck = productline.getProductList();
            for (Product productListOrphanCheckProduct : productListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Productline (" + productline + ") cannot be destroyed since the Product " + productListOrphanCheckProduct + " in its productList field has a non-nullable productline field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(productline);
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Productline> findProductlineEntities() {
        return findProductlineEntities(true, -1, -1);
    }

    public List<Productline> findProductlineEntities(int maxResults, int firstResult) {
        return findProductlineEntities(false, maxResults, firstResult);
    }

    private List<Productline> findProductlineEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Productline.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public Productline findProductline(String id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Productline.class, id);
        } finally {
            em.close();
        }
    }

    public int getProductlineCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Productline> rt = cq.from(Productline.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
