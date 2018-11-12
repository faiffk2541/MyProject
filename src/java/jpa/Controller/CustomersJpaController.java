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
import jpa.model.Register;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;
import jpa.Controller.exceptions.IllegalOrphanException;
import jpa.Controller.exceptions.NonexistentEntityException;
import jpa.Controller.exceptions.PreexistingEntityException;
import jpa.Controller.exceptions.RollbackFailureException;
import jpa.model.Customers;
import jpa.model.Orders;

/**
 *
 * @author Ploy
 */
public class CustomersJpaController implements Serializable {

    public CustomersJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Customers customers) throws PreexistingEntityException, RollbackFailureException, Exception {
        if (customers.getRegisterList() == null) {
            customers.setRegisterList(new ArrayList<Register>());
        }
        if (customers.getOrdersList() == null) {
            customers.setOrdersList(new ArrayList<Orders>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            List<Register> attachedRegisterList = new ArrayList<Register>();
            for (Register registerListRegisterToAttach : customers.getRegisterList()) {
                registerListRegisterToAttach = em.getReference(registerListRegisterToAttach.getClass(), registerListRegisterToAttach.getEmail());
                attachedRegisterList.add(registerListRegisterToAttach);
            }
            customers.setRegisterList(attachedRegisterList);
            List<Orders> attachedOrdersList = new ArrayList<Orders>();
            for (Orders ordersListOrdersToAttach : customers.getOrdersList()) {
                ordersListOrdersToAttach = em.getReference(ordersListOrdersToAttach.getClass(), ordersListOrdersToAttach.getOrdernumber());
                attachedOrdersList.add(ordersListOrdersToAttach);
            }
            customers.setOrdersList(attachedOrdersList);
            em.persist(customers);
            for (Register registerListRegister : customers.getRegisterList()) {
                Customers oldCustomernumberOfRegisterListRegister = registerListRegister.getCustomernumber();
                registerListRegister.setCustomernumber(customers);
                registerListRegister = em.merge(registerListRegister);
                if (oldCustomernumberOfRegisterListRegister != null) {
                    oldCustomernumberOfRegisterListRegister.getRegisterList().remove(registerListRegister);
                    oldCustomernumberOfRegisterListRegister = em.merge(oldCustomernumberOfRegisterListRegister);
                }
            }
            for (Orders ordersListOrders : customers.getOrdersList()) {
                Customers oldCustomernumberOfOrdersListOrders = ordersListOrders.getCustomernumber();
                ordersListOrders.setCustomernumber(customers);
                ordersListOrders = em.merge(ordersListOrders);
                if (oldCustomernumberOfOrdersListOrders != null) {
                    oldCustomernumberOfOrdersListOrders.getOrdersList().remove(ordersListOrders);
                    oldCustomernumberOfOrdersListOrders = em.merge(oldCustomernumberOfOrdersListOrders);
                }
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            if (findCustomers(customers.getCustomernumber()) != null) {
                throw new PreexistingEntityException("Customers " + customers + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Customers customers) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Customers persistentCustomers = em.find(Customers.class, customers.getCustomernumber());
            List<Register> registerListOld = persistentCustomers.getRegisterList();
            List<Register> registerListNew = customers.getRegisterList();
            List<Orders> ordersListOld = persistentCustomers.getOrdersList();
            List<Orders> ordersListNew = customers.getOrdersList();
            List<String> illegalOrphanMessages = null;
            for (Register registerListOldRegister : registerListOld) {
                if (!registerListNew.contains(registerListOldRegister)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Register " + registerListOldRegister + " since its customernumber field is not nullable.");
                }
            }
            for (Orders ordersListOldOrders : ordersListOld) {
                if (!ordersListNew.contains(ordersListOldOrders)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Orders " + ordersListOldOrders + " since its customernumber field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<Register> attachedRegisterListNew = new ArrayList<Register>();
            for (Register registerListNewRegisterToAttach : registerListNew) {
                registerListNewRegisterToAttach = em.getReference(registerListNewRegisterToAttach.getClass(), registerListNewRegisterToAttach.getEmail());
                attachedRegisterListNew.add(registerListNewRegisterToAttach);
            }
            registerListNew = attachedRegisterListNew;
            customers.setRegisterList(registerListNew);
            List<Orders> attachedOrdersListNew = new ArrayList<Orders>();
            for (Orders ordersListNewOrdersToAttach : ordersListNew) {
                ordersListNewOrdersToAttach = em.getReference(ordersListNewOrdersToAttach.getClass(), ordersListNewOrdersToAttach.getOrdernumber());
                attachedOrdersListNew.add(ordersListNewOrdersToAttach);
            }
            ordersListNew = attachedOrdersListNew;
            customers.setOrdersList(ordersListNew);
            customers = em.merge(customers);
            for (Register registerListNewRegister : registerListNew) {
                if (!registerListOld.contains(registerListNewRegister)) {
                    Customers oldCustomernumberOfRegisterListNewRegister = registerListNewRegister.getCustomernumber();
                    registerListNewRegister.setCustomernumber(customers);
                    registerListNewRegister = em.merge(registerListNewRegister);
                    if (oldCustomernumberOfRegisterListNewRegister != null && !oldCustomernumberOfRegisterListNewRegister.equals(customers)) {
                        oldCustomernumberOfRegisterListNewRegister.getRegisterList().remove(registerListNewRegister);
                        oldCustomernumberOfRegisterListNewRegister = em.merge(oldCustomernumberOfRegisterListNewRegister);
                    }
                }
            }
            for (Orders ordersListNewOrders : ordersListNew) {
                if (!ordersListOld.contains(ordersListNewOrders)) {
                    Customers oldCustomernumberOfOrdersListNewOrders = ordersListNewOrders.getCustomernumber();
                    ordersListNewOrders.setCustomernumber(customers);
                    ordersListNewOrders = em.merge(ordersListNewOrders);
                    if (oldCustomernumberOfOrdersListNewOrders != null && !oldCustomernumberOfOrdersListNewOrders.equals(customers)) {
                        oldCustomernumberOfOrdersListNewOrders.getOrdersList().remove(ordersListNewOrders);
                        oldCustomernumberOfOrdersListNewOrders = em.merge(oldCustomernumberOfOrdersListNewOrders);
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
                Integer id = customers.getCustomernumber();
                if (findCustomers(id) == null) {
                    throw new NonexistentEntityException("The customers with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Customers customers;
            try {
                customers = em.getReference(Customers.class, id);
                customers.getCustomernumber();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The customers with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Register> registerListOrphanCheck = customers.getRegisterList();
            for (Register registerListOrphanCheckRegister : registerListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Customers (" + customers + ") cannot be destroyed since the Register " + registerListOrphanCheckRegister + " in its registerList field has a non-nullable customernumber field.");
            }
            List<Orders> ordersListOrphanCheck = customers.getOrdersList();
            for (Orders ordersListOrphanCheckOrders : ordersListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Customers (" + customers + ") cannot be destroyed since the Orders " + ordersListOrphanCheckOrders + " in its ordersList field has a non-nullable customernumber field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(customers);
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

    public List<Customers> findCustomersEntities() {
        return findCustomersEntities(true, -1, -1);
    }

    public List<Customers> findCustomersEntities(int maxResults, int firstResult) {
        return findCustomersEntities(false, maxResults, firstResult);
    }

    private List<Customers> findCustomersEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Customers.class));
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

    public Customers findCustomers(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Customers.class, id);
        } finally {
            em.close();
        }
    }

    public int getCustomersCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Customers> rt = cq.from(Customers.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
