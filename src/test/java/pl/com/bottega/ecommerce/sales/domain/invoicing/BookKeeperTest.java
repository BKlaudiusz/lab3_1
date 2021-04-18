package pl.com.bottega.ecommerce.sales.domain.invoicing;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.ClientData;
import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.Id;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductData;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductDataBuilder;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductType;
import pl.com.bottega.ecommerce.sharedkernel.Money;



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

    
}