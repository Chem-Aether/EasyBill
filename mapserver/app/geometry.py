def _point_on_segment(point, start, end, epsilon=1e-10):
    x, y = point
    x1, y1 = start
    x2, y2 = end
    cross = (x - x1) * (y2 - y1) - (y - y1) * (x2 - x1)
    if abs(cross) > epsilon:
        return False
    return min(x1, x2) - epsilon <= x <= max(x1, x2) + epsilon and min(y1, y2) - epsilon <= y <= max(y1, y2) + epsilon


def _inside_ring(point, ring):
    inside = False
    x, y = point
    for index, start in enumerate(ring):
        end = ring[(index + 1) % len(ring)]
        if _point_on_segment(point, start, end):
            return True
        x1, y1 = start
        x2, y2 = end
        if (y1 > y) != (y2 > y):
            intersection = (x2 - x1) * (y - y1) / (y2 - y1) + x1
            if x < intersection:
                inside = not inside
    return inside


def _inside_polygon(point, polygon):
    if not polygon or not _inside_ring(point, polygon[0]):
        return False
    return not any(_inside_ring(point, hole) for hole in polygon[1:])


def contains(geometry, point):
    if geometry.get("type") == "Polygon":
        return _inside_polygon(point, geometry.get("coordinates", []))
    if geometry.get("type") == "MultiPolygon":
        return any(_inside_polygon(point, polygon) for polygon in geometry.get("coordinates", []))
    return False


def representative_point(feature):
    min_lon, min_lat, max_lon, max_lat = feature["bbox"]
    candidates = [[(min_lon + max_lon) / 2, (min_lat + max_lat) / 2]]
    geometry = feature["geometry"]
    polygons = [geometry["coordinates"]] if geometry["type"] == "Polygon" else geometry["coordinates"]
    for polygon in polygons:
        ring = polygon[0] if polygon else []
        points = ring[:-1] if len(ring) > 1 and ring[0] == ring[-1] else ring
        if points:
            candidates.append([sum(item[0] for item in points) / len(points), sum(item[1] for item in points) / len(points)])
            candidates.append(points[0])
    return next((candidate for candidate in candidates if contains(geometry, candidate)), candidates[0])
