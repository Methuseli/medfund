import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { DataTableComponent } from '../../shared/components/data-table/data-table.component';
import { StatCardComponent } from '../../shared/components/stat-card/stat-card.component';
import { MembersService, Member } from '../../core/services/members.service';

@Component({
  selector: 'app-members',
  standalone: true,
  imports: [CommonModule, FormsModule, DataTableComponent, StatCardComponent],
  templateUrl: './members.component.html',
  styleUrl: './members.component.scss',
})
export class MembersComponent implements OnInit {
  members: Member[] = [];
  filteredMembers: Member[] = [];
  searchQuery = '';
  activeCount = 0; enrolledCount = 0; suspendedCount = 0; terminatedCount = 0;

  columns = [
    { key: 'memberNumber', label: 'Member #' },
    { key: 'firstName', label: 'First Name' },
    { key: 'lastName', label: 'Last Name' },
    { key: 'email', label: 'Email' },
    { key: 'status', label: 'Status', type: 'status' },
    { key: 'enrollmentDate', label: 'Enrolled', type: 'date' },
  ];

  constructor(private membersService: MembersService) {}

  ngOnInit(): void {
    this.membersService.list().subscribe({
      next: (members) => {
        this.members = members;
        this.filteredMembers = members;
        this.activeCount = members.filter(m => m.status === 'active').length;
        this.enrolledCount = members.filter(m => m.status === 'enrolled').length;
        this.suspendedCount = members.filter(m => m.status === 'suspended').length;
        this.terminatedCount = members.filter(m => m.status === 'terminated').length;
      },
      error: () => {}
    });
  }

  onSearch(): void {
    const q = this.searchQuery.toLowerCase();
    this.filteredMembers = q
      ? this.members.filter(m => m.firstName.toLowerCase().includes(q) || m.lastName.toLowerCase().includes(q) || m.memberNumber.toLowerCase().includes(q))
      : this.members;
  }
}
