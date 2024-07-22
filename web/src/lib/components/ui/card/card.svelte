<script lang="ts">
  import { Icons } from '$lib/utils';
  import { BriefcaseBusiness, User, Users, Braces, House, Pencil } from 'lucide-svelte';

  export let icon: Icons;
  export let url: string;
  export let text: string;
  export let number: number;
</script>

<div
  class="w-1/4 max-w-[300px] h-1/4 rounded-xl bg-white p-4 justify-between flex flex-row top-[50%] transform shadow-custom"
>
  <div class="flex flex-col text-primary justify-between">
    <p class="font-normal text-xl">{text}</p>
    <p class="text-4xl text-bold mt-6">{number}</p>
    <p class="invisible">.</p>
  </div>
  <div class="flex flex-col justify-between">
    <div class="bg-primary flex p-2 rounded-lg justify-center">
      {#if icon === Icons.STUDENT}
        <User />
      {:else if icon === Icons.TEACHER}
        <BriefcaseBusiness />
      {:else if icon === Icons.GRADE}
        <Users />
      {:else if icon === Icons.ROOM}
        <House />
      {:else if icon === Icons.CONSTRAINT}
        <Braces />
      {/if}
    </div>

    <a href={url} class="bg-accent hover:bg-primary aspect-square rounded-lg flex items-center justify-center p-2">
      <Pencil class="stroke-white" />
    </a>
  </div>
</div>
