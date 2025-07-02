package com.media.zapp.service;

import com.media.zapp.dto.Edge;
import com.media.zapp.dto.GraphResponse;
import com.media.zapp.dto.Node;
import com.media.zapp.dto.UserSearchResult;
import com.media.zapp.entity.Connection;
import com.media.zapp.entity.User;
import com.media.zapp.repository.ConnectionRepository;
import com.media.zapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GraphService {

    private final UserRepository userRepository;
    private final ConnectionRepository connectionRepository;

    public GraphResponse getGraph(Integer userId, Integer depth) {
        // 1. Verify the root user exists
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("User not found");
        }

        Set<Integer> visited = new HashSet<>();
        visited.add(userId);
        Set<Integer> frontier = new HashSet<>();
        frontier.add(userId);
        Set<Edge> edges = new HashSet<>();

        // 2. Perform breadth-first traversal for specified depth
        for (int i = 0; i < depth; i++) {
            if (frontier.isEmpty()) {
                break;
            }

            // Fetch all edges touching nodes in current frontier
            List<Connection> fetchedConnections = connectionRepository.findConnectionsByUserIds(frontier);
            Set<Integer> newFrontier = new HashSet<>();

            for (Connection connection : fetchedConnections) {
                Integer u = connection.getUserId1();
                Integer v = connection.getUserId2();

                // Order ascending to maintain consistency
                Integer a = Math.min(u, v);
                Integer b = Math.max(u, v);
                edges.add(new Edge(a, b));

                // Add unseen nodes to new frontier
                if (!visited.contains(u)) {
                    newFrontier.add(u);
                }
                if (!visited.contains(v)) {
                    newFrontier.add(v);
                }
            }


            visited.addAll(newFrontier);
            frontier = newFrontier;
        }



        // 3. Load user details for all visited IDs
        List<User> users = userRepository.findByIdIn(visited);
        List<Node> nodes = users.stream()
                .map(user -> new Node(
                        user.getId(),
                        user.getUsername(),
                        user.getEmail(),
                        user.getFullName(),
                        user.getAvatarUrl()
                ))
                .toList();


        // GraphService.java (add these methods)




        return new GraphResponse(nodes, new ArrayList<>(edges));
    }

    public List<UserSearchResult> searchUsersByNamePrefix(Integer rootUserId, String prefix) {
        // Validate input
        if (prefix == null || prefix.trim().isEmpty()) {
            return Collections.emptyList();
        }
        String cleanPrefix = prefix.trim().toLowerCase();

        // Get connected user IDs within depth 3
        Set<Integer> connectedUserIds = getConnectedUserIds(rootUserId, 3);

        // Fetch matching users from database
        List<User> matchingUsers = userRepository.findByUsernameStartingWithIgnoreCase(cleanPrefix);

        // Filter and map results
        return matchingUsers.stream()
                .filter(user -> connectedUserIds.contains(user.getId()))
                .map(user -> new UserSearchResult(
                        user.getId(),
                        user.getUsername(),
                        user.getAvatarUrl(),
                        user.getFullName(),
                        calculateConnectionDepth(rootUserId, user.getId())  // Calculate connection depth
                ))
                .collect(Collectors.toList());
    }

    private Set<Integer> getConnectedUserIds(Integer rootUserId, int depth) {
        if (!userRepository.existsById(rootUserId)) {
            throw new RuntimeException("User not found");
        }

        Set<Integer> visited = new HashSet<>();
        visited.add(rootUserId);
        Set<Integer> frontier = new HashSet<>();
        frontier.add(rootUserId);

        for (int i = 0; i < depth; i++) {
            if (frontier.isEmpty()) break;

            List<Connection> connections = connectionRepository.findConnectionsByUserIds(frontier);
            Set<Integer> newFrontier = new HashSet<>();

            for (Connection conn : connections) {
                if (!visited.contains(conn.getUserId1())) {
                    newFrontier.add(conn.getUserId1());
                }
                if (!visited.contains(conn.getUserId2())) {
                    newFrontier.add(conn.getUserId2());
                }
            }

            visited.addAll(newFrontier);
            frontier = newFrontier;
        }

        return visited;
    }

    // GraphService.java

    private int calculateConnectionDepth(Integer sourceId, Integer targetId) {
        if (sourceId.equals(targetId)) return 0;

        // Batch fetch all connections for efficiency
        Set<Integer> allUserIds = new HashSet<>();
        allUserIds.add(sourceId);
        allUserIds.add(targetId);
        List<Connection> allConnections = connectionRepository.findConnectionsByUserIds(allUserIds);

        // Build adjacency list
        Map<Integer, List<Integer>> adjacency = new HashMap<>();
        for (Connection conn : allConnections) {
            adjacency.computeIfAbsent(conn.getUserId1(), k -> new ArrayList<>()).add(conn.getUserId2());
            adjacency.computeIfAbsent(conn.getUserId2(), k -> new ArrayList<>()).add(conn.getUserId1());
        }

        // BFS using adjacency list
        Map<Integer, Integer> depths = new HashMap<>();
        Queue<Integer> queue = new LinkedList<>();
        depths.put(sourceId, 0);
        queue.add(sourceId);

        while (!queue.isEmpty()) {
            int current = queue.poll();
            int currentDepth = depths.get(current);

            for (Integer neighbor : adjacency.getOrDefault(current, Collections.emptyList())) {
                if (!depths.containsKey(neighbor)) {
                    depths.put(neighbor, currentDepth + 1);

                    if (neighbor.equals(targetId)) {
                        return currentDepth + 1;
                    }

                    queue.add(neighbor);
                }
            }
        }

        return -1; // Not connected
    }

}
