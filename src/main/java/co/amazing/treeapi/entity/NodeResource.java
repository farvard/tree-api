package co.amazing.treeapi.entity;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import co.amazing.treeapi.controller.TreeController;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.ResourceSupport;

@Data
@NoArgsConstructor
public class NodeResource extends ResourceSupport {

    private final List<Link> links = new ArrayList<>();
    private Node node;

    public NodeResource(final Node node) {
        this.node = node;
        links.add(linkTo(methodOn(TreeController.class).findOne(node.getIdentifier())).withSelfRel());
        links.add(linkTo(methodOn(TreeController.class).findOne(node.getRoot())).withRel("root"));
        links.add(linkTo(methodOn(TreeController.class).findOne(node.getParent())).withRel("parent"));
        links.add(linkTo(methodOn(TreeController.class).children(node.getIdentifier())).withRel("children"));
    }
}