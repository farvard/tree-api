package co.amazing.treeapi.service;


import static co.amazing.treeapi.Constants.REDIS_CHILDREN_PREFIX;
import static co.amazing.treeapi.Constants.REDIS_HEIGHT_PREFIX;
import static co.amazing.treeapi.Constants.REDIS_ROOT;

import java.util.ArrayList;
import java.util.List;
import org.redisson.api.RSet;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VisualizationService {

    private final RedissonClient redis;

    @Autowired
    public VisualizationService(RedissonClient redis) {
        this.redis = redis;
    }

    public String csv() {
        List<String> csv = new ArrayList<>();
        csv.add("id,value");
        Long r = redis.<Long>getBucket(REDIS_ROOT).get();
        csv.addAll(children(r, null));
        return String.join("\n", csv);
    }

    private List<String> children(Long id, String prefix) {
        String p = (prefix == null ? "" : (prefix + ".")) + id;
        List<String> csv = new ArrayList<>();
        Long height = redis.<Long>getBucket(REDIS_HEIGHT_PREFIX + id).get();
        csv.add(p + "," + height);
        RSet<Long> children = redis.getSet(REDIS_CHILDREN_PREFIX + id);
        children.forEach(c -> csv.addAll(children(c, p)));
        return csv;
    }
}
