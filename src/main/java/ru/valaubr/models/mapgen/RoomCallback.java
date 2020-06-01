package ru.valaubr.models.mapgen;

import ru.valaubr.models.shared.geom.IntPoint;
import ru.valaubr.models.shared.geom.IntRect;

import java.util.List;

public interface RoomCallback {
	void processRoom(int room, IntRect roomRect, List<IntPoint> tiles);
}
