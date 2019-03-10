package com.crossal.recordreader.customer;

import com.crossal.recordreader.customer.helpers.CustomerStreamFactory;
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

import java.io.BufferedReader;
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
    private CustomerStreamFactory streamFactoryMock;
    @Mock
    private Page pageMock;
    @Mock
    private Pageable pageableMock;
    @Mock
    private BufferedReader readerMock;
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

    private List<Customer> customers;
    private String customer1Json;
    private String customer2Json;
    private String customer3Json;
    private String customer4Json;
    private String customer5Json;
    private String customerWithMissingDataJson;
    private String customerWithExtraDataJson;
    private String customerWithInvalidDataJson;
    private int CUSTOMER_SAVE_BATCH_SIZE = 3;
    private int CUSTOMER_GET_BATCH_SIZE = 3;

    @Before
    public void setupBefore() throws IOException {
        ReflectionTestUtils.setField(customerService, "CUSTOMER_SAVE_BATCH_SIZE", CUSTOMER_SAVE_BATCH_SIZE);
        ReflectionTestUtils.setField(customerService, "CUSTOMER_GET_BATCH_SIZE", CUSTOMER_GET_BATCH_SIZE);

        when(customerRepositoryMock.findAll(any(Pageable.class))).thenReturn(pageMock);
        when(pageMock.nextPageable()).thenReturn(pageableMock);

        when(streamFactoryMock.getReader(any(File.class))).thenReturn(readerMock);
        when(entityManager.unwrap(any())).thenReturn(sessionMock);

        customers = new ArrayList<>();
        Customer c1 = new Customer();
        Customer c2 = new Customer();
        customers.add(c1);
        customers.add(c2);

        customer1Json = "{\"latitude\": \"52.986375\", \"user_id\": 12, \"name\": \"Christina McArdle\", \"longitude\": \"-6.043701\"}";
        customer2Json = "{\"latitude\": \"51.92893\", \"user_id\": 1, \"name\": \"Alice Cahill\", \"longitude\": \"-10.27699\"}";
        customer3Json = "{\"latitude\": \"51.8856167\", \"user_id\": 2, \"name\": \"Ian McArdle\", \"longitude\": \"-10.4240951\"}";
        customer4Json = "{\"latitude\": \"52.3191841\", \"user_id\": 3, \"name\": \"Jack Enright\", \"longitude\": \"-8.5072391\"}";
        customer5Json = "{\"latitude\": \"53.807778\", \"user_id\": 28, \"name\": \"Charlie Halligan\", \"longitude\": \"-7.714444\"}";

        customerWithMissingDataJson = "{\"user_id\": 28, \"name\": \"Charlie Halligan\", \"longitude\": \"-7.714444\"}";
        customerWithExtraDataJson = "{\"some_file\": \"12345\", \"latitude\": \"53.807778\", \"user_id\": 28, \"name\": \"Charlie Halligan\", \"longitude\": \"-7.714444\"}";
        customerWithInvalidDataJson = "{\"latitude\": \"a string value\", \"user_id\": 28, \"name\": \"Charlie Halligan\", \"longitude\": \"-7.714444\"}";
    }

    @Test
    public void saveCustomersWithinKms_emptyFile_isOk() throws IOException {
        when(readerMock.readLine()).thenReturn(null);

        customerService.saveCustomersWithinKms(fileMock, 0);

        verify(customerRepositoryMock, times(0)).save(any(Customer.class));
    }

    @Test
    public void saveCustomersWithinKms_isOk() throws IOException {
        when(readerMock.readLine()).thenReturn(customer1Json, customer2Json, null);
        when(mathUtilMock.distanceWithinKms(anyDouble(), anyDouble(), anyDouble(), anyDouble(), anyInt())).thenReturn(true, false);

        customerService.saveCustomersWithinKms(fileMock, 0);

        verify(customerRepositoryMock, times(1)).save(any(Customer.class));
        verify(sessionMock, times(0)).flush();
    }

    @Test
    public void saveCustomersWithinKms_batchInsert_isOk() throws IOException {
        when(readerMock.readLine()).thenReturn(customer1Json, customer2Json, customer3Json, customer4Json, customer5Json, null);
        when(mathUtilMock.distanceWithinKms(anyDouble(), anyDouble(), anyDouble(), anyDouble(), anyInt())).thenReturn(true, false, true, true, true);

        customerService.saveCustomersWithinKms(fileMock, 0);

        verify(customerRepositoryMock, times(4)).save(any(Customer.class));
        verify(sessionMock, times(1)).flush();
    }

    @Test
    public void saveCustomersWithinKms_invalidJson_skipsCustomer() throws IOException {
        when(readerMock.readLine()).thenReturn(customerWithInvalidDataJson, customer1Json, null);
        when(mathUtilMock.distanceWithinKms(anyDouble(), anyDouble(), anyDouble(), anyDouble(), anyInt())).thenReturn(true);

        customerService.saveCustomersWithinKms(fileMock, 0);

        verify(customerRepositoryMock, times(1)).save(any(Customer.class));
        verify(sessionMock, times(0)).flush();
    }

    @Test
    public void saveCustomersWithinKms_missingCustomerData_skipsCustomer() throws IOException {
        when(readerMock.readLine()).thenReturn(customerWithMissingDataJson, customer1Json, null);
        when(mathUtilMock.distanceWithinKms(anyDouble(), anyDouble(), anyDouble(), anyDouble(), anyInt())).thenReturn(true);

        customerService.saveCustomersWithinKms(fileMock, 0);

        verify(customerRepositoryMock, times(1)).save(any(Customer.class));
        verify(sessionMock, times(0)).flush();
    }

    @Test
    public void saveCustomersWithinKms_extraCustomerData_isOk() throws IOException {
        when(readerMock.readLine()).thenReturn(customerWithExtraDataJson, customer1Json, null);
        when(mathUtilMock.distanceWithinKms(anyDouble(), anyDouble(), anyDouble(), anyDouble(), anyInt())).thenReturn(true);

        customerService.saveCustomersWithinKms(fileMock, 0);

        verify(customerRepositoryMock, times(1)).save(any(Customer.class));
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
}
