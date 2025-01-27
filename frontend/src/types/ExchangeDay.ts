import { ref } from "vue";

export interface ExchangeDay {
  id: number;
  name: string;
  startDate: number;
  endDate: number;
  description: string;
  location: Location;
}

export interface Location {
  id: number;
  street: string;
  houseNumber: string;
  postalCode: number;
  city: string;
  country: string;
}

export const exchangeDays = ref<ExchangeDay[]>([]);
export const selectedExchangeDay = ref<ExchangeDay | null>(null);
