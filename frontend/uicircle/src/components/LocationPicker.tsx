// src/components/LocationPicker.tsx
import { MapContainer, TileLayer, Marker, useMapEvents } from "react-leaflet";
import type { LatLngLiteral } from "leaflet";
import L from "leaflet";
import "leaflet/dist/leaflet.css";

// Use specified icons instead of the icons in the library
delete (L.Icon.Default.prototype as any)._getIconUrl;

const MARKER_ICON_PATHS = {
  iconUrl: "/images/marker-icon.png",
  iconRetinaUrl: "/images/marker-icon-2x.png",
  shadowUrl: "/images/marker-shadow.png",
};

L.Icon.Default.mergeOptions(MARKER_ICON_PATHS);

type LocationPickerProps = {
  value: LatLngLiteral | null;
  onChange: (coords: LatLngLiteral) => void;
};

// Inner component to handle click events on the map
const LocationClickHandler: React.FC<{ onSelect: (coords: LatLngLiteral) => void }> = ({
  onSelect,
}) => {
  useMapEvents({
    click(e) {
      onSelect(e.latlng);
    },
  });

  return null;
};

export const LocationPicker: React.FC<LocationPickerProps> = ({ value, onChange }) => {
  // Default center
  const defaultCenter: LatLngLiteral = value || { lat: 41.8686, lng: -87.6484 };

  return (
    <div className="w-full h-64 rounded-md overflow-hidden border border-gray-300">
      <MapContainer
        center={defaultCenter}
        zoom={13}
        style={{ width: "100%", height: "100%" }}
        scrollWheelZoom={true}
      >
        <TileLayer
          // OpenStreetMap free tile server
          attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OSM</a> contributors'
          url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
        />

        {/* Click handler */}
        <LocationClickHandler
          onSelect={(coords) => {
            onChange(coords);
          }}
        />

        {/* Show marker when we have a value */}
        {value && <Marker position={value} />}
      </MapContainer>
    </div>
  );
};
