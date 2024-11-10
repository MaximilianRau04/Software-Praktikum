import { ref } from 'vue';
export interface Event {
    id: number;
    name: string;
    date: number;
    description: string;
    endTime: string;
    startTime: string;
    room: string;
  }