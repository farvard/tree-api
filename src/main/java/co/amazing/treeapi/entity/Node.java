package co.amazing.treeapi.entity;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
@AllArgsConstructor
public class Node {

    private Long identifier;
    private Long parent;
    private Long root;
    private Long height;

}
