package co.amazing.treeapi.controller;

import co.amazing.treeapi.entity.Node;
import co.amazing.treeapi.entity.NodeResource;
import co.amazing.treeapi.entity.NodeResources;
import co.amazing.treeapi.service.TreeService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "nodes", produces = MediaType.APPLICATION_JSON_VALUE)
public class TreeController {

    private final TreeService treeService;

    @Autowired
    public TreeController(TreeService treeService) {
        this.treeService = treeService;
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity findOne(@PathVariable("id") Long nodeId) {
        Node node = treeService.findById(nodeId);
        return ResponseEntity.ok(new NodeResource(node));
    }

    @GetMapping(path = "/{id}/children")
    public ResponseEntity children(@PathVariable("id") Long nodeId) {
        List<Node> children = treeService.findChildren(nodeId);
        return ResponseEntity.ok(new NodeResources(nodeId, children));
    }

    @PatchMapping(path = "/{id}")
    public void changeParent(@PathVariable("id") Long nodeId, @RequestParam Long parentId) {
        treeService.changeParent(nodeId, parentId);
    }

}
