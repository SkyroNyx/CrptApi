import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import java.util.*;
import java.util.concurrent.TimeUnit;
import static java.lang.Thread.sleep;


public class CrptApi {
    private final long timeLimit;
    private final int requestLimit;
    private Queue<Long> requestsTimesQueue = new LinkedList<>();
    public CrptApi(TimeUnit timeUnit, long timeLimit, int requestLimit) {
        this.timeLimit = timeUnit.toMillis(timeLimit);
        this.requestLimit = requestLimit;
    }

    public synchronized void createDocument(Object document, String signature) {
        if (requestsTimesQueue.size() < requestLimit) {
            requestsTimesQueue.add(System.currentTimeMillis());
        }
        else if (requestsTimesQueue.size() == requestLimit) {
            if (System.currentTimeMillis() - requestsTimesQueue.peek() <= timeLimit) {
                try {
                    sleep(timeLimit - (System.currentTimeMillis() - requestsTimesQueue.peek()));
                    requestsTimesQueue.remove();
                    requestsTimesQueue.add(System.currentTimeMillis());
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            else {
                while (System.currentTimeMillis() - requestsTimesQueue.peek() > timeLimit) {
                    requestsTimesQueue.remove();
                }
                requestsTimesQueue.add(System.currentTimeMillis());
            }
        }
        String url = "https://ismp.crpt.ru/api/v3/1k/documents/create";
        try {
            HttpClient httpClient = HttpClients.createDefault();
            HttpPost httpPost = new HttpPost(url);
            String requestBody;
            ObjectMapper mapper = new ObjectMapper();
            try {
                requestBody = mapper.writeValueAsString(document);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            StringEntity entity = new StringEntity(requestBody);
            httpPost.setEntity(entity);
            httpPost.setHeader("Content-type", "application/json");
            httpPost.setHeader("Signature", signature);
            HttpResponse response = httpClient.execute(httpPost);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(2020, 0, 24);
        Date date = calendar.getTime();
        Document document = new DocumentBuilder()
                .setDescription(new Description("string"))
                .setDocId("string")
                .setDocStatus("string")
                .setDocType("LP_INTRODUCE_GOODS")
                .setImportRequest(true)
                .setOwnerInn("string")
                .setParticipantInn("string")
                .setProducerInn("string")
                .setProductionDate(date)
                .setProductionType("string")
                .addProduct(new ProductBuilder()
                        .setCertificateDocument("string")
                        .setCertificateDocumentDate(date)
                        .setCertificateDocumentNumber("string")
                        .setOwnerInn("string")
                        .setProducerInn("string")
                        .setProductionDate(date)
                        .setTnvedCode("string")
                        .setUitCode("string")
                        .setUituCode("string")
                        .build())
                .setRegDate(date)
                .setRegNumber("string")
                .build();
        ObjectMapper mapper = new ObjectMapper();
        try {
            System.out.println(mapper.writeValueAsString(document));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
//        CrptApi crptApi = new CrptApi(TimeUnit.SECONDS, 10,  5);
//        crptApi.createDocument(document, "signature");
        //        for (int i = 0; i < 10000; i++) {
//            new Thread(() -> crptApi.createDocument(document, "signature")).start();
//        }
    }
}

class Document {
    private Description description;
    @JsonProperty("doc_id")
    private String docId;
    @JsonProperty("doc_status")
    private String docStatus;
    @JsonProperty("doc_type")
    private String docType;
    private boolean importRequest;
    @JsonProperty("owner_inn")
    private String ownerInn;
    @JsonProperty("participant_inn")
    private String participantInn;
    @JsonProperty("producer_inn")
    private String producerInn;
    @JsonFormat(pattern = "yyyy-MM-dd")
    @JsonProperty("production_date")
    private Date productionDate;
    @JsonProperty("production_type")
    private String productionType;
    private List<Product> products = new ArrayList<>();
    @JsonFormat(pattern = "yyyy-MM-dd")
    @JsonProperty("reg_date")
    private Date regDate;
    @JsonProperty("reg_number")
    private String regNumber;

    public Description getDescription() {
        return description;
    }

    public void setDescription(Description description) {
        this.description = description;
    }

    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }

    public String getDocStatus() {
        return docStatus;
    }

    public void setDocStatus(String docStatus) {
        this.docStatus = docStatus;
    }

    public String getDocType() {
        return docType;
    }

    public void setDocType(String docType) {
        this.docType = docType;
    }

    public boolean isImportRequest() {
        return importRequest;
    }

    public void setImportRequest(boolean importRequest) {
        this.importRequest = importRequest;
    }

    public String getOwnerInn() {
        return ownerInn;
    }

    public void setOwnerInn(String ownerInn) {
        this.ownerInn = ownerInn;
    }

    public String getParticipantInn() {
        return participantInn;
    }

    public void setParticipantInn(String participantInn) {
        this.participantInn = participantInn;
    }

    public String getProducerInn() {
        return producerInn;
    }

    public void setProducerInn(String producerInn) {
        this.producerInn = producerInn;
    }

    public Date getProductionDate() {
        return productionDate;
    }

    public void setProductionDate(Date productionDate) {
        this.productionDate = productionDate;
    }

    public String getProductionType() {
        return productionType;
    }

    public void setProductionType(String productionType) {
        this.productionType = productionType;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public Date getRegDate() {
        return regDate;
    }

    public void setRegDate(Date regDate) {
        this.regDate = regDate;
    }

    public String getRegNumber() {
        return regNumber;
    }

    public void setRegNumber(String regNumber) {
        this.regNumber = regNumber;
    }
}

class Description {
    public Description(String participating) {
        this.participating = participating;
    }

    public String getParticipating() {
        return participating;
    }

    public void setParticipating(String participating) {
        this.participating = participating;
    }

    private String participating;
}

class Product {
    @JsonProperty("certificate_document")
    private String certificateDocument;
    @JsonFormat(pattern = "yyyy-MM-dd")
    @JsonProperty("certificate_document_date")
    private Date certificateDocumentDate;
    @JsonProperty("certificate_document_number")
    private String certificateDocumentNumber;
    @JsonProperty("owner_inn")
    private String ownerInn;
    @JsonProperty("producer_inn")
    private String producerInn;
    @JsonFormat(pattern = "yyyy-MM-dd")
    @JsonProperty("production_date")
    private Date productionDate;
    @JsonProperty("tnved_code")
    private String tnvedCode;
    @JsonProperty("uit_code")
    private String uitCode;
    @JsonProperty("uitu_code")
    private String uituCode;

    public String getCertificateDocument() {
        return certificateDocument;
    }

    public void setCertificateDocument(String certificateDocument) {
        this.certificateDocument = certificateDocument;
    }

    public Date getCertificateDocumentDate() {
        return certificateDocumentDate;
    }

    public void setCertificateDocumentDate(Date certificateDocumentDate) {
        this.certificateDocumentDate = certificateDocumentDate;
    }

    public String getCertificateDocumentNumber() {
        return certificateDocumentNumber;
    }

    public void setCertificateDocumentNumber(String certificateDocumentNumber) {
        this.certificateDocumentNumber = certificateDocumentNumber;
    }

    public String getOwnerInn() {
        return ownerInn;
    }

    public void setOwnerInn(String ownerInn) {
        this.ownerInn = ownerInn;
    }

    public String getProducerInn() {
        return producerInn;
    }

    public void setProducerInn(String producerInn) {
        this.producerInn = producerInn;
    }

    public Date getProductionDate() {
        return productionDate;
    }

    public void setProductionDate(Date productionDate) {
        this.productionDate = productionDate;
    }

    public String getTnvedCode() {
        return tnvedCode;
    }

    public void setTnvedCode(String tnvedCode) {
        this.tnvedCode = tnvedCode;
    }

    public String getUitCode() {
        return uitCode;
    }

    public void setUitCode(String uitCode) {
        this.uitCode = uitCode;
    }

    public String getUituCode() {
        return uituCode;
    }

    public void setUituCode(String uituCode) {
        this.uituCode = uituCode;
    }
}

class DocumentBuilder {
    private Document document = new Document();

    public DocumentBuilder setDescription(Description description) {
        document.setDescription(description);
        return this;
    }

    public DocumentBuilder setDocId(String doc_id) {
        document.setDocId(doc_id);
        return this;
    }

    public DocumentBuilder setDocStatus(String doc_status) {
        document.setDocStatus(doc_status);
        return this;
    }

    public DocumentBuilder setDocType(String doc_type) {
        document.setDocType(doc_type);
        return this;
    }

    public DocumentBuilder setImportRequest(boolean importRequest) {
        document.setImportRequest(importRequest);
        return this;
    }

    public DocumentBuilder setOwnerInn(String ownerInn) {
        document.setOwnerInn(ownerInn);
        return this;
    }

    public DocumentBuilder setParticipantInn(String participantInn) {
        document.setParticipantInn(participantInn);
        return this;
    }

    public DocumentBuilder setProducerInn(String producerInn) {
        document.setProducerInn(producerInn);
        return this;
    }

    public DocumentBuilder setProductionDate(Date productionDate) {
        document.setProductionDate(productionDate);
        return this;
    }

    public DocumentBuilder setProductionType(String productionType) {
        document.setProductionType(productionType);
        return this;
    }

    public DocumentBuilder addProduct(Product product) {
        document.getProducts().add(product);
        return this;
    }

    public DocumentBuilder setRegDate(Date regDate) {
        document.setRegDate(regDate);
        return this;
    }

    public DocumentBuilder setRegNumber(String regNumber) {
        document.setRegNumber(regNumber);
        return this;
    }

    public Document build() {
        return document;
    }
}

class ProductBuilder {
    private Product product = new Product();

    public ProductBuilder setCertificateDocument(String certificateDocument) {
        product.setCertificateDocument(certificateDocument);
        return this;
    }

    public ProductBuilder setCertificateDocumentDate(Date certificateDocumentDate) {
        product.setCertificateDocumentDate(certificateDocumentDate);
        return this;
    }

    public ProductBuilder setCertificateDocumentNumber(String certificateDocumentNumber) {
        product.setCertificateDocumentNumber(certificateDocumentNumber);
        return this;
    }

    public ProductBuilder setOwnerInn(String ownerInn) {
        product.setOwnerInn(ownerInn);
        return this;
    }

    public ProductBuilder setProducerInn(String producerInn) {
        product.setProducerInn(producerInn);
        return this;
    }

    public ProductBuilder setProductionDate(Date productionDate) {
        product.setProductionDate(productionDate);
        return this;
    }

    public ProductBuilder setTnvedCode(String tnvedCode) {
        product.setTnvedCode(tnvedCode);
        return this;
    }

    public ProductBuilder setUitCode(String uitCode) {
        product.setUitCode(uitCode);
        return this;
    }

    public ProductBuilder setUituCode(String uituCode) {
        product.setUituCode(uituCode);
        return this;
    }

    public Product build() {
        return product;
    }
}