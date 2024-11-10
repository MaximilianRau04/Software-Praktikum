import { ref } from 'vue';

export interface ExchangeDay {
  id: number;
  name: string;
  date: number;
  description: string;
  location: string;
  events: number[];
}

export const exchangeDays = ref<ExchangeDay[]>([]);
export const selectedExchangeDay = ref<ExchangeDay | null>(null);
