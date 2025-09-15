package vn.backend.redis_cache.controller;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.backend.redis_cache.service.RedisService;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/test/redis")
@RequiredArgsConstructor
@Slf4j
public class TestRedisController {
    private final RedisService redisService;

    /**
     * Test basic set/get
     * POST /api/test/redis/set
     * Body: {"key": "test-key", "value": "test-value"}
     */
    @PostMapping("/set")
    public ResponseEntity<Map<String, Object>> testSet(@RequestBody TestRequest request) {
        Map<String, Object> response = new HashMap<>();

        try {
            if (request.getTtl() != null && request.getTtl() > 0) {
                redisService.set(request.getKey(), request.getValue(), request.getTtl(), TimeUnit.SECONDS);
                response.put("message", "Set with TTL successfully");
                response.put("ttl", request.getTtl() + " seconds");
            } else {
                redisService.set(request.getKey(), request.getValue());
                response.put("message", "Set successfully");
            }

            response.put("key", request.getKey());
            response.put("value", request.getValue());
            response.put("success", true);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error in test set", e);
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * Test get
     * GET /api/test/redis/get/{key}
     */
    @GetMapping("/get/{key}")
    public ResponseEntity<Map<String, Object>> testGet(@PathVariable String key) {
        Map<String, Object> response = new HashMap<>();

        try {
            Object value = redisService.get(key);
            boolean exists = redisService.existsKey(key);
            long ttl = redisService.getTTL(key);

            response.put("key", key);
            response.put("value", value);
            response.put("exists", exists);
            response.put("ttl", ttl);
            response.put("success", true);

            if (value != null) {
                response.put("message", "Found value");
            } else {
                response.put("message", "Key not found or expired");
            }

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error in test get", e);
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * Test delete
     * DELETE /api/test/redis/delete/{key}
     */
    @DeleteMapping("/delete/{key}")
    public ResponseEntity<Map<String, Object>> testDelete(@PathVariable String key) {
        Map<String, Object> response = new HashMap<>();

        try {
            boolean deleted = redisService.delete(key);

            response.put("key", key);
            response.put("deleted", deleted);
            response.put("success", true);

            if (deleted) {
                response.put("message", "Key deleted successfully");
            } else {
                response.put("message", "Key not found or already deleted");
            }

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error in test delete", e);
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * Health check Redis connection
     * GET /api/test/redis/health
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> response = new HashMap<>();

        try {
            String testKey = "health-check-" + System.currentTimeMillis();
            String testValue = "OK";

            // Test set
            redisService.set(testKey, testValue, 10, TimeUnit.SECONDS);

            // Test get
            Object retrieved = redisService.get(testKey);

            // Test delete
            boolean deleted = redisService.delete(testKey);

            boolean allTestsPassed = testValue.equals(retrieved) && deleted;

            response.put("redis_connection", "Connected");
            response.put("set_test", "Passed");
            response.put("get_test", testValue.equals(retrieved) ? "Passed" : "Failed");
            response.put("delete_test", deleted ? "Passed" : "Failed");
            response.put("overall_status", allTestsPassed ? "HEALTHY" : "UNHEALTHY");
            response.put("success", true);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Redis health check failed", e);
            response.put("redis_connection", "Failed");
            response.put("error", e.getMessage());
            response.put("overall_status", "UNHEALTHY");
            response.put("success", false);
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @Data
    public static class TestRequest {
        private String key;
        private Object value;
        private Long ttl; // TTL in seconds (optional)
    }
}
