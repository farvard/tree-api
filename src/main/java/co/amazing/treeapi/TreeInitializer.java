package co.amazing.treeapi;


import static co.amazing.treeapi.Constants.REDIS_CHILDREN_PREFIX;
import static co.amazing.treeapi.Constants.REDIS_HEIGHT_PREFIX;
import static co.amazing.treeapi.Constants.REDIS_PARENT_PREFIX;
import static co.amazing.treeapi.Constants.REDIS_ROOT;

import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import org.apache.commons.lang3.RandomUtils;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

@Component
public class TreeInitializer {

    private static final Logger LOG = LoggerFactory.getLogger(TreeInitializer.class);
    private final RedissonClient redis;
    private long lastIndex = 0;

    @Value("${create.random.tree}")
    private boolean createRandomTree;

    @Value("${create.tree.from.csv}")
    private boolean createTreeFromCsv;

    @Value("${create.tree.from.csv.path}")
    private String createTreeFromCsvPath;

    @Autowired
    public TreeInitializer(RedissonClient redis) {
        this.redis = redis;
    }

    @PostConstruct
    public void init() throws Exception {
        if (createRandomTree) {
            LOG.info("creating random tree.");
            randomTree();
            LOG.info("finished with {} nodes", lastIndex);
        } else if (createTreeFromCsv) {
            LOG.info("creating tree from csv: {}", createTreeFromCsvPath);
            int count = treeFromCSV(createTreeFromCsvPath);
            LOG.info("finished with {} nodes", count);
        }
    }

    private void randomTree() {
        redis.getKeys().flushall();
        long root = nexId();
        redis.<Long>getBucket(REDIS_ROOT).set(root);
        redis.<Long>getBucket(REDIS_HEIGHT_PREFIX + root).set(0L);
        List<Long> ids = addNodes(root, 1);
        redis.getSet(REDIS_CHILDREN_PREFIX + root).addAll(ids);
    }

    private List<Long> addNodes(long parent, long h) {
        long MAX = 5;
        List<Long> ids = new ArrayList<>();
        if (MAX - h < 2) {
            return ids;
        }
        long c = RandomUtils.nextLong(MAX - h, MAX);
        for (int i = 0; i < c; i++) {
            long id = nexId();
            ids.add(id);
            redis.<Long>getBucket(REDIS_PARENT_PREFIX + id).set(parent);
            redis.<Long>getBucket(REDIS_HEIGHT_PREFIX + id).set(h);
            redis.getSet(REDIS_CHILDREN_PREFIX + id).addAll(addNodes(id, h + 1));
        }
        return ids;
    }

    private long nexId() {
        if (lastIndex % 1000 == 0) {
            LOG.info("lastIndex={}", lastIndex);
        }
        return ++lastIndex;
    }

    public int treeFromCSV(String path) throws IOException {
        redis.getKeys().flushall();
        List<String[]> lines = readCSV(path);
        lines.forEach(l -> addNode(l[0].trim(), l[1].trim()));
        Long root = redis.<Long>getBucket(REDIS_ROOT).get();
        setHeights(root, 0L);
        return lines.size();
    }

    private void setHeights(Long root, Long height) {
        redis.<Long>getBucket(REDIS_HEIGHT_PREFIX + root).set(height);
        for (Long c : redis.<Long>getSet(REDIS_CHILDREN_PREFIX + root)) {
            setHeights(c, height + 1);
        }
    }

    private List<String[]> readCSV(String path) throws IOException {
        List<String> lines = Files.readAllLines(new ClassPathResource(path).getFile().toPath());
        return lines.stream().map(l -> l.split(",")).collect(Collectors.toList());
    }

    private void addNode(String nodeId, String parentId) {
        if (parentId.equals("null")) {
            redis.<Long>getBucket(REDIS_ROOT).set(Long.parseLong(nodeId));
        } else {
            redis.<Long>getBucket(REDIS_PARENT_PREFIX + nodeId).set(Long.parseLong(parentId));
            redis.getSet(REDIS_CHILDREN_PREFIX + parentId).add(Long.parseLong(nodeId));
        }
    }

}
