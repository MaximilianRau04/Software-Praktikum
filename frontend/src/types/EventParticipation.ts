import { User } from "./User";
import { Event } from "./Event";

export interface EventParticipation {
  id: number;
  user: User;
  event: Event;
  confirmed: boolean;
  confirmationTime: string;
}
