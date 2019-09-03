package co.amazing.treeapi.service;

import static co.amazing.treeapi.Constants.REDIS_CHILDREN_PREFIX;
import static co.amazing.treeapi.Constants.REDIS_HEIGHT_PREFIX;
import static co.amazing.treeapi.Constants.REDIS_PARENT_PREFIX;
import static co.amazing.treeapi.Constants.REDIS_ROOT;

import co.amazing.treeapi.entity.Node;
import co.amazing.treeapi.exception.NodeNotFoundException;
import co.amazing.treeapi.exception.ParentChangeException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.redisson.api.RBucket;
import org.redisson.api.RTransaction;
import org.redisson.api.RedissonClient;
import org.redisson.api.TransactionOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TreeService {

    private final RedissonClient redis;

    @Autowired
    public TreeService(RedissonClient redis) {
        this.redis = redis;
    }

    public void changeParent(Long nodeId, Long newParentId) {
        Long root = redis.<Long>getBucket(REDIS_ROOT).get();
        if (nodeId.equals(newParentId)) {
            throw new ParentChangeException("Could not change parent of node to itself.");
        }
        if (nodeId.equals(root)) {
            throw new ParentChangeException("Could not change parent of root.");
        }
        Long preParentId = redis.<Long>getBucket(REDIS_PARENT_PREFIX + nodeId).get();
        if (preParentId == null) {
            throw new NodeNotFoundException(nodeId);
        }
        Long newParenParentId = redis.<Long>getBucket(REDIS_PARENT_PREFIX + newParentId).get();
        if (newParenParentId == null && !newParentId.equals(root)) {
            throw new NodeNotFoundException(nodeId);
        }
        if (preParentId.equals(newParentId)) {
            return;
        }
        RTransaction transaction = redis.createTransaction(TransactionOptions.defaults());
        long height = transaction.<Long>getBucket(REDIS_HEIGHT_PREFIX + newParentId).get() + 1;
        transaction.<Long>getBucket(REDIS_PARENT_PREFIX + nodeId).set(newParentId);
        transaction.getSet(REDIS_CHILDREN_PREFIX + newParentId).add(nodeId);
        transaction.getSet(REDIS_CHILDREN_PREFIX + preParentId).remove(nodeId);
        changeHeights(transaction, nodeId, height, newParentId);
        transaction.commit();
    }

    private void changeHeights(RTransaction transaction, Long nodeId, Long height, Long newParentId) {
        if (nodeId.equals(newParentId)) {
            throw new ParentChangeException("Could not change parent of node to its own child.");
        }
        RBucket<Long> nodeHeight = transaction.getBucket(REDIS_HEIGHT_PREFIX + nodeId);
        nodeHeight.set(height);
        Set<Long> children = redis.getSet(REDIS_CHILDREN_PREFIX + nodeId);
        children.forEach(c -> changeHeights(transaction, c, height + 1, newParentId));
    }

    public List<Node> findChildren(Long nodeId) {
        Node node = findById(nodeId);
        Set<Long> children = redis.getSet(REDIS_CHILDREN_PREFIX + node.getIdentifier());
        return children.stream()
              .map(i -> new Node(i, nodeId, node.getRoot(), node.getHeight() + 1))
              .collect(Collectors.toList());
    }

    public Node findById(Long nodeId) {
        Long parent = redis.<Long>getBucket(REDIS_PARENT_PREFIX + nodeId).get();
        Long height = redis.<Long>getBucket(REDIS_HEIGHT_PREFIX + nodeId).get();
        Long root = redis.<Long>getBucket(REDIS_ROOT).get();
        if (!nodeId.equals(root) && parent == null) {
            throw new NodeNotFoundException(nodeId);
        }
        return new Node(nodeId, parent, root, height);
    }

}
