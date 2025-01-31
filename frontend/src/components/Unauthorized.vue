<template>
    <div>
        <div>401 Unauthorized</div>
        <button @click="home">Zurück zum Dashboard</button>
    </div>
</template>

<script>
import { useRouter } from 'vue-router';

const home = () => {
    const router = useRouter();
    router.push('/login')
}
</script>