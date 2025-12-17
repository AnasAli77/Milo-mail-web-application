import { Component, OnInit, inject, signal, ChangeDetectionStrategy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';

export interface StatsDTO {
    sentThisWeek: number;
    received: number;
    unread: number;
    topContact: string;
    topContactCount: number;
    priorityBreakdown: { [key: number]: number };
}

@Component({
    selector: 'app-stats',
    imports: [CommonModule],
    templateUrl: './stats.html',
    styleUrls: ['./stats.css'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class StatsComponent implements OnInit {
    private http = inject(HttpClient);

    stats = signal<StatsDTO | null>(null);
    loading = signal(true);
    error = signal<string | null>(null);

    ngOnInit() {
        this.loadStats();
    }

    loadStats() {
        this.loading.set(true);
        this.http.get<StatsDTO>(`${environment.baseUrl}/stats`).subscribe({
            next: (data) => {
                this.stats.set(data);
                this.loading.set(false);
            },
            error: (err) => {
                console.error('Failed to load stats:', err);
                this.error.set('Failed to load statistics');
                this.loading.set(false);
            }
        });
    }

    // Calculate percentage for priority bar
    getPriorityPercent(priority: number) {
        const breakdown = this.stats()?.priorityBreakdown;
        if (!breakdown) return 0;

        const total = Object.values(breakdown).reduce((a, b) => a + b, 0);
        if (total === 0) return 0;

        return ((breakdown[priority] || 0) / total * 100).toFixed(1);
    }

    // Get total emails for priority calculation
    getTotalEmails(): number {
        const breakdown = this.stats()?.priorityBreakdown;
        if (!breakdown) return 0;
        return Object.values(breakdown).reduce((a, b) => a + b, 0);
    }

    getPriorityLabel(priority: number): string {
        switch (priority) {
            case 1: return 'Very Low';
            case 2: return 'Low';
            case 3: return 'Normal';
            case 4: return 'High';
            case 5: return 'Extreme';
            default: return 'Unknown';
        }
    }

    getPriorityColor(priority: number): string {
        switch (priority) {
            case 1: return '#94a3b8'; // gray
            case 2: return '#22c55e'; // green
            case 3: return '#3b82f6'; // blue
            case 4: return '#f59e0b'; // amber
            case 5: return '#ef4444'; // red
            default: return '#94a3b8';
        }
    }
}
