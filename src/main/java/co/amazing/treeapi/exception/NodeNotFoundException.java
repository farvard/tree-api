package co.amazing.treeapi.exception;

public class NodeNotFoundException extends RuntimeException {

    public NodeNotFoundException(Long nodeId) {
        super("Node " + nodeId + " not found.");
    }
}
