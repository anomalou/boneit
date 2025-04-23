package org.anomalou.model;

import lombok.AllArgsConstructor;

/**
 * Author: Aleksandr Borodin
 * Creation date: 4/23/25
 */
@AllArgsConstructor
public class Line {
    
    public FPoint p1;
    public FPoint p2;
    
    public FPoint findIntersectionHorisontal(double k) {
        double x1 = p1.x, y1 = p1.y;
        double x2 = p2.x, y2 = p2.y;
        
        // Проверяем, лежит ли k между y1 и y2
        if (Math.min(y1, y2) <= k && k <= Math.max(y1, y2)) {
            if (y1 == y2) {
                // Отрезок горизонтальный, проверяем совпадение y
                if (y1 == k) {
                    return new FPoint(x1, k); // Любая точка отрезка
                } else {
                    return null; // Нет пересечения
                }
            }
            
            // Вычисляем параметр t
            double t = (k - y1) / (y2 - y1);
            double x = x1 + t * (x2 - x1);
            return new FPoint(x, k);
        }
        
        return null; // Нет пересечения
    }
    
    public FPoint findIntersectionVertical(double k) {
        double x1 = p1.x, y1 = p1.y;
        double x2 = p2.x, y2 = p2.y;
        
        // Проверяем, лежит ли k между x1 и x2
        if (Math.min(x1, x2) <= k && k <= Math.max(x1, x2)) {
            if (x1 == x2) {
                // Отрезок вертикальный, проверяем совпадение x
                if (x1 == k) {
                    return new FPoint(k, y1); // Любая точка отрезка
                } else {
                    return null; // Нет пересечения
                }
            }
            
            // Вычисляем параметр t
            double t = (k - x1) / (x2 - x1);
            double y = y1 + t * (y2 - y1);
            return new FPoint(k, y);
        }
        
        return null; // Нет пересечения
    }
    
}
