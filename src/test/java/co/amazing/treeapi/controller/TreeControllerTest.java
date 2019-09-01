package co.amazing.treeapi.controller;

import co.amazing.treeapi.TestConfig;
import co.amazing.treeapi.entity.Node;
import co.amazing.treeapi.entity.NodeResource;
import co.amazing.treeapi.entity.NodeResources;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
public class TreeControllerTest {

    @ClassRule
    public static final TestConfig config = new TestConfig();
    @Autowired
    private WebTestClient api;

    @Test
    public void findOne() {
        long nodeId = 3;
        Node expected = new Node(nodeId, 1L, 1L, 1L);
        this.api.get().uri("/nodes/" + nodeId).exchange()
              .expectStatus().isOk()
              .expectBody(NodeResource.class).value(c -> Assert.assertEquals(expected, c.getNode()));
    }

    @Test
    public void findOneHeight1() {
        long nodeId = 2;
        Node expected = new Node(nodeId, 1L, 1L, 1L);
        this.api.get().uri("/nodes/" + nodeId).exchange()
              .expectStatus().isOk()
              .expectBody(NodeResource.class).value(c -> Assert.assertEquals(expected, c.getNode()));
    }

    @Test
    public void findOneHeight2() {
        long nodeId = 17;
        Node expected = new Node(nodeId, 3L, 1L, 2L);
        this.api.get().uri("/nodes/" + nodeId).exchange()
              .expectStatus().isOk()
              .expectBody(NodeResource.class).value(c -> Assert.assertEquals(expected, c.getNode()));
    }

    @Test
    public void findOneHeight3() {
        long nodeId = 9;
        Node expected = new Node(nodeId, 8L, 1L, 3L);
        this.api.get().uri("/nodes/" + nodeId).exchange()
              .expectStatus().isOk()
              .expectBody(NodeResource.class).value(c -> Assert.assertEquals(expected, c.getNode()));
    }


    @Test
    public void findRoot() {
        long nodeId = 1;
        Node expected = new Node(1L, null, 1L, 0L);
        this.api.get().uri("/nodes/" + nodeId).exchange()
              .expectStatus().isOk()
              .expectBody(NodeResource.class).value(c -> Assert.assertEquals(expected, c.getNode()));
    }

    @Test
    public void findByInvalidId() {
        long nodeId = 30;
        this.api.get().uri("/nodes/" + nodeId).exchange().expectStatus().isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void childrenOfRoot() {
        long nodeId = 1L;
        this.api.get().uri("/nodes/" + nodeId + "/children").exchange()
              .expectStatus().isOk().expectBody(NodeResources.class)
              .value(c -> Assert.assertEquals(3, c.getContent().size()));
    }

    @Test
    public void childrenOfValidId() {
        long nodeId = 10L;
        this.api.get().uri("/nodes/" + nodeId + "/children").exchange()
              .expectStatus().isOk().expectBody(NodeResources.class)
              .value(c -> Assert.assertEquals(2, c.getContent().size()));
    }

    @Test
    public void childrenOfInvalidId() {
        long nodeId = 30L;
        this.api.get().uri("/nodes/" + nodeId + "/children").exchange().expectStatus().isEqualTo(HttpStatus.NOT_FOUND);
    }


    @Test
    public void changeParentOfValidNodeToValidNode() {
        long nodeId = 5;
        long newParentId = 19;
        this.api.patch().uri("/nodes/" + nodeId + "?parentId=" + newParentId)
              .exchange().expectStatus().isOk();
    }

    @Test
    public void changeParentOfValidNodeToInvalidNode() {
        long nodeId = 2;
        long newParentId = 40;
        this.api.patch().uri("/nodes/" + nodeId + "?parentId=" + newParentId)
              .exchange().expectStatus().isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void changeParentOfInvalidNodeToValidNode() {
        long nodeId = 45;
        long newParentId = 4;
        this.api.patch().uri("/nodes/" + nodeId + "?parentId=" + newParentId)
              .exchange().expectStatus().isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void changeParentOfInvalidNodeToInvalidNode() {
        long nodeId = 45;
        long newParentId = 46;
        this.api.patch().uri("/nodes/" + nodeId + "?parentId=" + newParentId)
              .exchange().expectStatus().isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void changeParentOfRootToValidNode() {
        long nodeId = 1;
        long newParentId = 4;
        this.api.patch().uri("/nodes/" + nodeId + "?parentId=" + newParentId)
              .exchange().expectStatus().isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void changeParentOfRootToInvalidNode() {
        long nodeId = 1;
        long newParentId = 45;
        this.api.patch().uri("/nodes/" + nodeId + "?parentId=" + newParentId)
              .exchange().expectStatus().isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void changeParentOfValidNoeToItsOwnChild() {
        long nodeId = 2;
        long newParentId = 19;
        this.api.patch().uri("/nodes/" + nodeId + "?parentId=" + newParentId)
              .exchange().expectStatus().isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void changeParentOfValidNodeToItself() {
        long nodeId = 2;
        long newParentId = 2;
        this.api.patch().uri("/nodes/" + nodeId + "?parentId=" + newParentId)
              .exchange().expectStatus().isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void changeParentOfValidNodeToItsParent() {
        long nodeId = 6;
        long newParentId = 2;
        this.api.patch().uri("/nodes/" + nodeId + "?parentId=" + newParentId)
              .exchange().expectStatus().isOk();
    }

}