import { MapContainer, TileLayer, Marker, Popup } from "react-leaflet";
import "leaflet/dist/leaflet.css";
import L from "leaflet";

// Fix for default marker icon issue in React-Leaflet
import icon from "leaflet/dist/images/marker-icon.png";
import iconShadow from "leaflet/dist/images/marker-shadow.png";

let DefaultIcon = L.icon({
  iconUrl: icon,
  shadowUrl: iconShadow,
  iconSize: [25, 41],
  iconAnchor: [12, 41],
});

L.Marker.prototype.options.icon = DefaultIcon;

interface LocationMapProps {
  latitude: number;
  longitude: number;
  title: string;
}

const LocationMap = ({ latitude, longitude, title }: LocationMapProps) => {

  return (
    <div className="mt-2 pt-2">
      <h2 className="text-lg font-bold text-gray-900 mb-4">
        Meetup location
      </h2>
      <div className="rounded-lg overflow-hidden border border-gray-200">
        <MapContainer
          center={[latitude, longitude]}
          zoom={15}
          style={{ height: "400px", width: "100%" }}
          scrollWheelZoom={false}
        >
          <TileLayer
            attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
            url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
          />
          <Marker position={[latitude, longitude]}>
            <Popup>
              <div className="text-sm">
                <p className="font-semibold">{title}</p>
                <p className="text-gray-600 text-xs mt-1">
                  Meetup location for this item
                </p>
              </div>
            </Popup>
          </Marker>
        </MapContainer>
      </div>
    </div>
  );
};

export default LocationMap;
