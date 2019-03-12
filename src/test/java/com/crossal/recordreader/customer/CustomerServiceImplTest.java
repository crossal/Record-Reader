package com.crossal.recordreader.customer;

import com.crossal.recordreader.helpers.FileJsonObjReader;
import com.crossal.recordreader.helpers.FileReaderFactory;
import com.crossal.recordreader.utils.MathUtil;
import org.hibernate.Session;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;

import javax.persistence.EntityManager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
public class CustomerServiceImplTest {

    @InjectMocks
    private CustomerServiceImpl customerService;
    @Mock
    private CustomerRepository customerRepositoryMock;
    @Mock
    private Page pageMock;
    @Mock
    private Pageable pageableMock;
    @Mock
    private File fileMock;
    @Mock
    private MathUtil mathUtilMock;
    @Mock
    private Session sessionMock;
    /**
     * this must have the same name as the variable in the customer service so mockito can differentiate it from 'sessionMock' of the same type
     */
    @Mock
    private EntityManager entityManager;
    @Mock
    private FileReaderFactory fileReaderFactoryMock;
    @Mock
    private FileJsonObjReader fileJsonObjReaderMock;

    private List<Customer> customers;
    private Customer customer1;
    private Customer customer2;
    private Customer customer3;
    private Customer customer4;
    private Customer customer5;
    private Customer customerWithMissingData;
    private int CUSTOMER_SAVE_BATCH_SIZE = 3;
    private int CUSTOMER_GET_BATCH_SIZE = 3;

    @Before
    public void setupBefore() throws IOException {
        ReflectionTestUtils.setField(customerService, "CUSTOMER_SAVE_BATCH_SIZE", CUSTOMER_SAVE_BATCH_SIZE);
        ReflectionTestUtils.setField(customerService, "CUSTOMER_GET_BATCH_SIZE", CUSTOMER_GET_BATCH_SIZE);

        when(customerRepositoryMock.findAll(any(Pageable.class))).thenReturn(pageMock);
        when(pageMock.nextPageable()).thenReturn(pageableMock);

        when(fileReaderFactoryMock.getJsonObjReader(any())).thenReturn(fileJsonObjReaderMock);
        when(entityManager.unwrap(any())).thenReturn(sessionMock);

        customers = new ArrayList<>();
        Customer c1 = new Customer();
        Customer c2 = new Customer();
        customers.add(c1);
        customers.add(c2);

        customer1 = generateCustomer(1);
        customer2 = generateCustomer(2);
        customer3 = generateCustomer(3);
        customer4 = generateCustomer(4);
        customer5 = generateCustomer(5);
        customerWithMissingData = generateCustomer(6);
        customerWithMissingData.setLongitude(null);
    }

    @Test
    public void saveCustomersWithinKms_emptyFile_isOk() throws IOException {
        when(fileJsonObjReaderMock.readObj(Customer.class)).thenReturn(null);

        customerService.saveCustomersWithinKms(fileMock, 0);

        verify(customerRepositoryMock, times(0)).save(any(Customer.class));
    }

    @Test
    public void saveCustomersWithinKms_isOk() throws IOException {
        when(fileJsonObjReaderMock.readObj(Customer.class)).thenReturn(customer1, customer2, null);
        when(mathUtilMock.distanceWithinKms(anyDouble(), anyDouble(), anyDouble(), anyDouble(), anyInt())).thenReturn(true, false);

        customerService.saveCustomersWithinKms(fileMock, 0);

        verify(customerRepositoryMock, times(1)).save(any(Customer.class));
        verify(sessionMock, times(0)).flush();
    }

    @Test
    public void saveCustomersWithinKms_batchInsert_isOk() throws IOException {
        when(fileJsonObjReaderMock.readObj(Customer.class)).thenReturn(customer1, customer2, customer3, customer4, customer5, null);
        when(mathUtilMock.distanceWithinKms(anyDouble(), anyDouble(), anyDouble(), anyDouble(), anyInt())).thenReturn(true, false, true, true, true);

        customerService.saveCustomersWithinKms(fileMock, 0);

        verify(customerRepositoryMock, times(4)).save(any(Customer.class));
        verify(sessionMock, times(1)).flush();
    }

    @Test
    public void saveCustomersWithinKms_missingCustomerData_skipsCustomer() throws IOException {
        when(fileJsonObjReaderMock.readObj(Customer.class)).thenReturn(customerWithMissingData, customer1, null);
        when(mathUtilMock.distanceWithinKms(anyDouble(), anyDouble(), anyDouble(), anyDouble(), anyInt())).thenReturn(true);

        customerService.saveCustomersWithinKms(fileMock, 0);

        verify(customerRepositoryMock, times(1)).save(customer1);
        verify(sessionMock, times(0)).flush();
    }

    @Test
    public void printCustomers_noPages_isOk() {
        when(pageMock.getContent()).thenReturn(new ArrayList());
        when(pageMock.hasNext()).thenReturn(false);
        customerService.printCustomers();
        verify(pageMock, times(0)).nextPageable();
    }

    @Test
    public void printCustomers_onePage_isOk() {
        when(pageMock.getContent()).thenReturn(customers);
        when(pageMock.hasNext()).thenReturn(false);
        customerService.printCustomers();
        verify(pageMock, times(0)).nextPageable();
    }

    @Test
    public void printCustomers_twoPages_isOk() {
        when(pageMock.getContent()).thenReturn(customers);
        when(pageMock.hasNext()).thenReturn(true, false);
        customerService.printCustomers();
        verify(pageMock, times(1)).nextPageable();
    }

    private Customer generateCustomer(int id) {
        Customer customer = new Customer();

        customer.setId(id);
        customer.setLatitude(123.0);
        customer.setLongitude(456.0);
        customer.setName("John Doe");

        return customer;
    }
}
