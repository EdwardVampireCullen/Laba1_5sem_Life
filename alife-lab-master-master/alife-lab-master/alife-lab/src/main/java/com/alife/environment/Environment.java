package com.alife.environment;

import com.alife.model.Agent;
import com.alife.model.Position;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * Пространственная модель: двумерная дискретная сетка (матрица).
 * В одной клетке может находиться только один агент.
 * Карта замкнута барьерами: координаты за пределами поля
 * считаются невалидными, агенты не могут выйти за границы.
 */
@Getter
public class Environment {

    private final int width;
    private final int height;
    private final Agent[][] grid;

    public Environment(int width, int height) {
        this.width = width;
        this.height = height;
        this.grid = new Agent[height][width];
    }

    /** Проверка, что координаты внутри поля. */
    public boolean inBounds(int x, int y) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }

    public Agent getAgent(int x, int y) {
        if (!inBounds(x, y)) {
            return null;
        }
        return grid[y][x];
    }

    public List<Position> getOrthogonalNeighbors(int x, int y) {
        List<Position> result = new ArrayList<>();
        int[][] dirs = {{0,-1},{0,1},{-1,0},{1,0}};
        for (int[] d : dirs) {
            int nx = x + d[0];
            int ny = y + d[1];
            if (inBounds(nx, ny)) {
                result.add(new Position(nx, ny));
            }
        }
        return result;
    }

    public void setAgent(int x, int y, Agent agent) {
        if (!inBounds(x, y)) {
            return; // за пределами поля ничего не ставим
        }
        grid[y][x] = agent;
        if (agent != null) {
            agent.setX(x);
            agent.setY(y);
        }
    }

    public void removeAgent(int x, int y) {
        if (inBounds(x, y)) {
            grid[y][x] = null;
        }
    }

    public boolean isEmpty(int x, int y) {
        return getAgent(x, y) == null;
    }

    /**
     * Возвращает только те соседние клетки, которые реально
     * существуют на карте. Клетки за границей не добавляются.
     */
    public List<Position> getNeighbors(int x, int y, int radius) {
        List<Position> result = new ArrayList<>();
        for (int dy = -radius; dy <= radius; dy++) {
            int maxDx = radius - Math.abs(dy); // ромб
            for (int dx = -maxDx; dx <= maxDx; dx++) {
                if (dx == 0 && dy == 0) continue;
                int nx = x + dx;
                int ny = y + dy;
                if (inBounds(nx, ny)) {
                    result.add(new Position(nx, ny));
                }
            }
        }
        return result;
    }

    /**
     * Пытается переместить агента в новую позицию.
     * Если клетка вне поля или занята — возвращает false,
     * агент остаётся на месте.
     */
    public boolean tryMove(Agent agent, int newX, int newY) {
        if (!inBounds(newX, newY)) {
            return false; // упёрлись в стену
        }
        if (grid[newY][newX] != null) {
            return false; // клетка занята
        }
        int oldX = agent.getX();
        int oldY = agent.getY();
        grid[oldY][oldX] = null;
        grid[newY][newX] = agent;
        agent.setX(newX);
        agent.setY(newY);
        return true;
    }
}