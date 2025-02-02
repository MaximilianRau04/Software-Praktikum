<template>
  <div id="map-container" ref="mapContainer"></div>
</template>

<script>
import L from 'leaflet';
import 'leaflet/dist/leaflet.css';

export default {
  props: ['coordinates'],
  data() {
    return {
      map: null,
      marker: null
    };
  },
  mounted() {
    if (this.coordinates) {
      this.$nextTick(() => {
        this.initMap();
      });
    }
  },
  watch: {
    coordinates(newCoords) {
      if (this.map && newCoords) {
        this.updateMap(newCoords);
      }
    }
  },
  methods: {
    initMap() {
      try {
        this.map = L.map(this.$refs.mapContainer).setView(
          [this.coordinates.lat, this.coordinates.lon], 
          15
        );
        
        L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
          attribution: '© OpenStreetMap contributors'
        }).addTo(this.map);

        this.createMarker(this.coordinates);
      } catch (error) {
        console.error('Map init error:', error);
      }
    },
    updateMap(newCoords) {
      this.map.setView([newCoords.lat, newCoords.lon], 15);
      if (this.marker) {
        this.marker.setLatLng([newCoords.lat, newCoords.lon]);
      } else {
        this.createMarker(newCoords);
      }
    },
    createMarker(coords) {
      if (!this.map) return;
      
      this.marker = L.marker([coords.lat, coords.lon])
        .addTo(this.map);
    }
  },
  beforeUnmount() {
    if (this.map) {
      this.map.remove();
      this.map = null;
    }
  }
};
</script>

<style scoped>
#map-container {
  height: 200px;
  width: 100%;
  border-radius: 8px;
}
</style>