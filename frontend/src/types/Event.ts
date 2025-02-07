import type { Location } from "./ExchangeDay";

export interface Event {
  id: number;
  name: string;
  description: string;
  date: string;
  endTime: string;
  exchangeDayName: string;
  startTime: string;
  room: Room;
  forumThreads: object[];
  inviteOnly: boolean;
}

export interface Room {
  name: string;
  id: number;
  type: string;
  location: Location;
  capacity: number;
  description: string;
  availability: number;
}
