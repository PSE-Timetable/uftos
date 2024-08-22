<script lang="ts">
  import { UserCog, Calendar } from 'lucide-svelte';
</script>

<div class="flex flex-row justify-start bg-foreground p-4 text-white">
  <h1 class="text-xl font-bold">UFTOS</h1>
</div>

<div class="flex flex-col gap-8 p-4 bg-background">
  <h1 class="text-xl font-bold">Wohin willst du gehen?</h1>
  <div class="flex flex-row justify-evenly">
    <div class="flex flex-col gap-2 items-center">
      <a href="/admin" class="flex justify-center items-center bg-white p-4 w-full rounded-lg hover:bg-accent">
        <UserCog class="w-[10vw] h-[10vw] stroke-primary hover:stroke-white" />
      </a>
      <p class="text-xl font-bold">Adminseite</p>
    </div>
    <div class="flex flex-col gap-2 items-center">
      <a
        href="/timetable/teacher"
        class="flex justify-center items-center bg-white p-4 w-full rounded-lg hover:bg-accent"
      >
        <Calendar class="w-[10vw] h-[10vw] stroke-primary hover:stroke-white" />
      </a>
      <p class="text-xl font-bold">Stundenplanseite</p>
    </div>
  </div>
</div>
