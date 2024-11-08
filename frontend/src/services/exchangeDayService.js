import apiClient from './api';

export default {
    // Request all available exchange days from backend
    getAllExchangeDays() {
        return apiClient.get('/exchange-days');
    },

    // Request all events for a certain exchange day provided by exchangeDayId
    getEventsByExchangeDayId(exchangeDayId) {
        return apiClient.get(`/events/exchange-day/${exchangeDayId}`);
    }
};