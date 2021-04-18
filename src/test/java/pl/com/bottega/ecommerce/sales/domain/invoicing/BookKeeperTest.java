package pl.com.bottega.ecommerce.sales.domain.invoicing;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.ClientData;
import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.Id;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductData;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductDataBuilder;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductType;
import pl.com.bottega.ecommerce.sharedkernel.Money;

import java.util.Date;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class BookKeeperTest {

    @Mock
    private TaxPolicy taxPolicy;

    @Mock
    private InvoiceFactory invoiceFactory;

    private BookKeeper keeper;

    private final ClientData  clientData = new ClientData(new Id("id"), "Nowak");
    private final ProductData productData = new ProductDataBuilder().name("test").price(Money.ZERO).type(ProductType.DRUG).productId(new Id("id")).snapshotDate(null).build();

    @Captor
    ArgumentCaptor<ProductType> productTypeCaptor;

    @Captor
    ArgumentCaptor<Money> moneyCaptor;


    @BeforeEach
    void setUp() {
        keeper = new BookKeeper(invoiceFactory);
    }


    @Test
    void returnOneItemWhenOneItemInInvoice(){
        InvoiceRequest invoiceRequest = new InvoiceRequest(clientData);
        invoiceRequest.add(new RequestItem(productData, 1, Money.ZERO));
        when(invoiceFactory.create(clientData)).thenReturn( new Invoice(Id.generate(), clientData));
        when(taxPolicy.calculateTax(any(ProductType.class), any(Money.class))).thenReturn(new Tax(Money.ZERO, "zero"));
        Invoice invoice= keeper.issuance(invoiceRequest, taxPolicy);
        assertEquals(1, invoice.getItems().size());
    }

    @Test
    void InvokeCalculateTaxTwiceTimeWhenTwoItem() {
        InvoiceRequest request = new InvoiceRequest(clientData);
        request.add( new RequestItem(productData, 1, Money.ZERO));
        request.add( new RequestItem(productData, 1, Money.ZERO));
        Invoice invoice = new Invoice(Id.generate(), clientData);
        when(invoiceFactory.create(clientData)).thenReturn(invoice);
        when(taxPolicy.calculateTax(any(ProductType.class), any(Money.class))).thenReturn( new Tax(Money.ZERO, "test"));
        keeper.issuance(request, taxPolicy);
        verify(taxPolicy, times(2)).calculateTax(productTypeCaptor.capture(), moneyCaptor.capture());
        List<ProductType> productTypesCopy = productTypeCaptor.getAllValues();
        List<Money> moneyListCopy = moneyCaptor.getAllValues();
        assertEquals(ProductType.DRUG, productTypesCopy.get(0));
        assertEquals(ProductType.DRUG, productTypesCopy.get(1));
        assertEquals(Money.ZERO, moneyListCopy.get(0));
        assertEquals(Money.ZERO, moneyListCopy.get(1));
    }

    @Test
    void returnZeroItemWhenZeroItemInInvoice() {
        InvoiceRequest invoiceRequest = new InvoiceRequest(clientData);
        when(invoiceFactory.create(clientData)).thenReturn(new Invoice(Id.generate(), clientData));
        Invoice invoice = keeper.issuance(invoiceRequest, taxPolicy);
        assertEquals(0, invoice.getItems().size());
    }
    @Test
    void addCalculatedTax() {
        Tax tax = new Tax(Money.ZERO,"test");
        InvoiceRequest request = new InvoiceRequest(clientData);
        Invoice invoice = new Invoice(Id.generate(), clientData);
        request.add(new RequestItem(productData, 1, Money.ZERO));
        when(invoiceFactory.create(any(ClientData.class))).thenReturn(invoice);
        when(taxPolicy.calculateTax(any(ProductType.class), any(Money.class))).thenReturn(tax);
        Invoice actualInvoice = keeper.issuance(request, taxPolicy);
        Tax actualTax = actualInvoice.getItems().get(0).getTax();
        assertEquals(tax, actualTax);
    }

    @Test
    void InvokeCalculateTaxZeroTimeWhenZeroItem() {
        InvoiceRequest request = new InvoiceRequest(clientData);
        Invoice invoice = new Invoice(Id.generate(), clientData);
        when(invoiceFactory.create(clientData)).thenReturn(invoice);
        keeper.issuance(request, taxPolicy);
        verify(taxPolicy, times(0)).calculateTax(productTypeCaptor.capture(), moneyCaptor.capture());
    }

}