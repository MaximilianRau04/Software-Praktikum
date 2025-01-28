<template>
    <div id="map-container" ref="mapContainer"></div>
  </template>
  
  <script>
  import L from 'leaflet';
  import 'leaflet/dist/leaflet.css';
  
  export default {
    props: ['coordinates'],
    mounted() {
      if (this.coordinates) {
        this.initMap();
      }
    },
    methods: {
      initMap() {
        const map = L.map(this.$refs.mapContainer).setView(
          [this.coordinates.lat, this.coordinates.lon], 
          15
        );
  
        L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
          attribution: '© OpenStreetMap contributors'
        }).addTo(map);
  
        L.marker([this.coordinates.lat, this.coordinates.lon])
          .addTo(map)
          .bindPopup(this.$parent.selectedExchangeDay?.location.street);
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