import 'package:flutter/material.dart';

class DashboardScreen extends StatelessWidget {
  const DashboardScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Dashboard')),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            const Text('Welcome Back', style: TextStyle(fontSize: 24, fontWeight: FontWeight.bold)),
            const SizedBox(height: 16),
            Row(
              children: [
                Expanded(child: _StatCard(label: 'Benefits Used', value: '45%', color: Colors.blue)),
                const SizedBox(width: 12),
                Expanded(child: _StatCard(label: 'Claims', value: '3', color: Colors.orange)),
              ],
            ),
            const SizedBox(height: 12),
            Row(
              children: [
                Expanded(child: _StatCard(label: 'Balance', value: '\$12,500', color: Colors.green)),
                const SizedBox(width: 12),
                Expanded(child: _StatCard(label: 'Next Payment', value: 'Apr 15', color: Colors.purple)),
              ],
            ),
            const SizedBox(height: 24),
            const Text('Recent Claims', style: TextStyle(fontSize: 18, fontWeight: FontWeight.w600)),
            const SizedBox(height: 12),
            _ClaimTile(claimNumber: 'CLM-001234', status: 'Approved', amount: '\$450.00', date: 'Mar 15, 2026'),
            _ClaimTile(claimNumber: 'CLM-001233', status: 'Pending', amount: '\$1,200.00', date: 'Mar 10, 2026'),
            _ClaimTile(claimNumber: 'CLM-001230', status: 'Paid', amount: '\$320.00', date: 'Feb 28, 2026'),
            const SizedBox(height: 24),
            const Text('Quick Actions', style: TextStyle(fontSize: 18, fontWeight: FontWeight.w600)),
            const SizedBox(height: 12),
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceEvenly,
              children: [
                _QuickAction(icon: Icons.receipt_long, label: 'View Claims'),
                _QuickAction(icon: Icons.health_and_safety, label: 'Benefits'),
                _QuickAction(icon: Icons.payment, label: 'Pay Bill'),
                _QuickAction(icon: Icons.qr_code, label: 'My Card'),
              ],
            ),
          ],
        ),
      ),
    );
  }
}

class _StatCard extends StatelessWidget {
  final String label;
  final String value;
  final Color color;
  const _StatCard({required this.label, required this.value, required this.color});

  @override
  Widget build(BuildContext context) {
    return Card(
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(label, style: TextStyle(color: Colors.grey[600], fontSize: 12)),
            const SizedBox(height: 4),
            Text(value, style: TextStyle(fontSize: 22, fontWeight: FontWeight.bold, color: color)),
          ],
        ),
      ),
    );
  }
}

class _ClaimTile extends StatelessWidget {
  final String claimNumber, status, amount, date;
  const _ClaimTile({required this.claimNumber, required this.status, required this.amount, required this.date});

  @override
  Widget build(BuildContext context) {
    final statusColor = switch (status) {
      'Approved' || 'Paid' => Colors.green,
      'Pending' => Colors.orange,
      'Rejected' => Colors.red,
      _ => Colors.grey,
    };
    return Card(
      margin: const EdgeInsets.only(bottom: 8),
      child: ListTile(
        title: Text(claimNumber, style: const TextStyle(fontWeight: FontWeight.w600)),
        subtitle: Text(date),
        trailing: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          crossAxisAlignment: CrossAxisAlignment.end,
          children: [
            Text(amount, style: const TextStyle(fontWeight: FontWeight.bold)),
            Container(
              padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 2),
              decoration: BoxDecoration(color: statusColor.withValues(alpha: 0.1), borderRadius: BorderRadius.circular(8)),
              child: Text(status, style: TextStyle(color: statusColor, fontSize: 12, fontWeight: FontWeight.w600)),
            ),
          ],
        ),
      ),
    );
  }
}

class _QuickAction extends StatelessWidget {
  final IconData icon;
  final String label;
  const _QuickAction({required this.icon, required this.label});

  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        CircleAvatar(radius: 24, child: Icon(icon)),
        const SizedBox(height: 4),
        Text(label, style: const TextStyle(fontSize: 11)),
      ],
    );
  }
}
