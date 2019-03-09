package com.crossal.recordreader.customer;

import com.crossal.recordreader.customer.helpers.CustomerStreamFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
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
//@SpringBootTest
@ActiveProfiles("test")
public class CustomerServiceImplTest {

    @InjectMocks
    private CustomerServiceImpl customerService;
    @Mock
    private EntityManager entityManagerMock;
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

    private List<Customer> customers;
    private String customer1Json;
    private String customer2Json;

    @Before
    public void setupBefore() throws IOException {
        ReflectionTestUtils.setField(customerService, "CUSTOMER_SAVE_BATCH_SIZE", 5);
        ReflectionTestUtils.setField(customerService, "CUSTOMER_GET_BATCH_SIZE", 5);

        when(customerRepositoryMock.findAll(any(Pageable.class))).thenReturn(pageMock);
        when(pageMock.nextPageable()).thenReturn(pageableMock);

        when(streamFactoryMock.getReader(any(File.class))).thenReturn(readerMock);

        customers = new ArrayList<>();
        Customer c1 = new Customer();
        Customer c2 = new Customer();
        customers.add(c1);
        customers.add(c2);

        customer1Json = "{\"latitude\": \"52.986375\", \"user_id\": 12, \"name\": \"Christina McArdle\", \"longitude\": \"-6.043701\"}";
        customer2Json = "{\"latitude\": \"51.92893\", \"user_id\": 1, \"name\": \"Alice Cahill\", \"longitude\": \"-10.27699\"}";
    }

    @Test
    public void saveCustomersWithinKms_emptyFile_isOk() throws IOException {
        when(readerMock.readLine()).thenReturn(null);
        customerService.saveCustomersWithinKms(fileMock, 0);
    }

    @Test
    public void saveCustomersWithinKms_isOk() throws IOException {
        when(readerMock.readLine()).thenReturn(customer1Json, customer2Json);
        customerService.saveCustomersWithinKms(fileMock, 0);
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
