import { ref } from "vue";

export interface ExchangeDay {
  id: number;
  name: string;
  startDate: number;
  endDate: number;
  description: string;
  location: string;
}

export const exchangeDays = ref<ExchangeDay[]>([]);
export const selectedExchangeDay = ref<ExchangeDay | null>(null);
