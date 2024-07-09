<script lang="ts">
    import ConstraintInstanceSelect from '$lib/elements/ui/constraint-instance-select/constraint-instance-select.svelte';
    import {
        type ConstraintSignature,
        type Grade, ParameterType,
        type Room,
        type StudentGroup, type Subject,
        type Tag,
        type Teacher,
        type Timeslot
    } from "$lib/sdk/fetch-client";
    import {Button} from "$lib/elements/ui/button";


    export let constraintSignature: ConstraintSignature;
    export let teachers: Teacher[] = [];
    export let rooms: Room[] = [];
    export let grades: Grade[] = [];
    export let tags: Tag[] = [];
    export let timeslots: Timeslot[] = [];
    export let studentGroups: StudentGroup[] = [];
    export let subjects: Subject[] = [];
</script>

<div class="flex flex-col gap-8 bg-primary w-fit p-6 rounded-md text-white">
    <p class="font-bold text-md">{ constraintSignature.description }</p>
    {#each constraintSignature.parameters as parameter}
        {#if parameter.parameterType === ParameterType.Teacher}
            <ConstraintInstanceSelect teachers={teachers} constraintParameter={parameter}/>
        {:else if parameter.parameterType === ParameterType.Room}
            <ConstraintInstanceSelect rooms={rooms} constraintParameter={parameter}/>
        {:else if parameter.parameterType === ParameterType.Grade}
            <ConstraintInstanceSelect grades={grades} constraintParameter={parameter}/>
        {:else if parameter.parameterType === ParameterType.Tag}
            <ConstraintInstanceSelect tags={tags} constraintParameter={parameter}/>
        {:else if parameter.parameterType === ParameterType.Timeslot}
            <ConstraintInstanceSelect timeslots={timeslots} constraintParameter={parameter}/>
        {:else if parameter.parameterType === ParameterType.StudentGroup}
            <ConstraintInstanceSelect studentGroups={studentGroups} constraintParameter={parameter}/>
        {:else if parameter.parameterType === ParameterType.Subject}
            <ConstraintInstanceSelect subjects={subjects} constraintParameter={parameter}/>
        {/if}
    {/each}

    <Button variant="outline" class="bg-accent border-0 text-md text-white py-6">Hinzufügen</Button>
</div>