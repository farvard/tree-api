package co.amazing.treeapi.entity;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import co.amazing.treeapi.controller.TreeController;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.hal.ResourcesMixin;

@Getter
@NoArgsConstructor
public class NodeResources extends ResourcesMixin {

    @JsonIgnore
    private Long parent;
    private List<NodeResource> content = new ArrayList<>();

    public NodeResources(Long parent, List<Node> content) {
        this.parent = parent;
        this.content = content.stream().map(NodeResource::new).collect(Collectors.toList());
    }

    @Override
    public List<Link> getLinks() {
        Link s = linkTo(methodOn(TreeController.class).children(getParent())).withSelfRel();
        Link p = linkTo(methodOn(TreeController.class).findOne(getParent())).withRel("parent");
        return Arrays.asList(s, p);
    }

}